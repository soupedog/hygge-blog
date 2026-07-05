package hygge.blog.job;

import hygge.blog.domain.local.bo.CacheObjectContainer;
import hygge.blog.domain.local.po.FileInfo;
import hygge.blog.domain.local.po.base.FileInfoBase;
import hygge.blog.domain.local.po.view.FileInfoView;
import hygge.blog.job.item.RefreshFileJobItem;
import hygge.blog.job.key.RefreshFileJobKey;
import hygge.blog.job.other.BaseBlogExclusiveJob;
import hygge.blog.job.other.HyggeBlogJpaContext;
import hygge.blog.repository.database.FileInfoDao;
import hygge.blog.repository.database.FileInfoViewDao;
import hygge.blog.service.local.CacheServiceImpl;
import hygge.blog.service.local.EventServiceImpl;
import hygge.blog.service.local.FileServiceImpl;
import hygge.blog.service.local.inner.file.FileOperationResult;
import hygge.blog.service.local.inner.file.FileOperationTool;
import hygge.blog.service.local.normal.PermissionServiceImpl;
import hygge.commons.exception.InternalRuntimeException;
import hygge.job.HyggeJobBatchItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Xavier
 * @date 2026/7/4
 */
@Service
public class RefreshFileCacheJob extends BaseBlogExclusiveJob<RefreshFileJobItem<FileInfo>, FileInfoView, FileInfo> {
    private final FileInfoViewDao fileInfoViewDao;
    private final FileInfoDao fileInfoDao;
    private final FileServiceImpl fileService;
    private final EventServiceImpl eventService;
    private final CacheServiceImpl cacheService;

    public RefreshFileCacheJob(FileInfoViewDao fileInfoViewDao, FileInfoDao fileInfoDao, FileServiceImpl fileService, EventServiceImpl eventService, CacheServiceImpl cacheService) {
        this.fileInfoViewDao = fileInfoViewDao;
        this.fileInfoDao = fileInfoDao;
        this.fileService = fileService;
        this.eventService = eventService;
        this.cacheService = cacheService;
    }

    @Override
    protected String getJobName() {
        return this.getClass().getSimpleName();
    }

    @Override
    protected List<FileInfoView> firstFetchIfNecessary(HyggeBlogJpaContext<FileInfoView> context, HyggeJobBatchItem<RefreshFileJobItem<FileInfo>> jobBatchItem) {
        Pageable pageable = PageRequest.of(0, context.getBatchSize(), Sort.by(Sort.Order.asc("fileId")));

        Page<FileInfoView> page = fileInfoViewDao.findAll(pageable);
        context.setPage(page);

        if (page.isLast()) {
            context.setNoNextPage(true);
        }

        return page.getContent();
    }

    @Override
    protected List<FileInfoView> getNextBatch(HyggeBlogJpaContext<FileInfoView> context, HyggeJobBatchItem<RefreshFileJobItem<FileInfo>> jobBatchItem) {
        if (context.isNoNextPage()) {
            return List.of();
        }

        Page<FileInfoView> page = context.getPage();
        page = fileInfoViewDao.findAll(page.nextPageable());
        context.setPage(page);

        if (page.isLast()) {
            context.setNoNextPage(true);
        }

        return page.getContent();
    }

    @Override
    protected RefreshFileJobItem<FileInfo> createJobItem(HyggeBlogJpaContext<FileInfoView> context, HyggeJobBatchItem<RefreshFileJobItem<FileInfo>> jobBatchItem, FileInfoView rawData) {
        return new RefreshFileJobItem<>(rawData);
    }

    @Override
    protected FileInfo handleSingleItem(HyggeBlogJpaContext<FileInfoView> context, RefreshFileJobItem<FileInfo> jobItem) {
        FileInfoView fileInfoView = jobItem.getRawData();
        FileInfo result = null;

        // 只处理公开可见类型文件
        if (PermissionServiceImpl._PUBLIC.getPermissionId().equals(fileInfoView.getPermissionId())) {
            if (context.getFileObject(RefreshFileJobKey.IS_UPDATE_MODE)) {
                // 更新操作
                Optional<FileInfo> fileInfoTemp = fileService.findFileFromDB(fileInfoView.getFileNo());
                FileInfo fileInfo = fileInfoTemp.orElseThrow(() -> new InternalRuntimeException(getJobName() + " FileInfo(" + fileInfoView.getFileNo() + ") was not found."));
                FileOperationResult copyResult = FileOperationTool.copyFile(true, fileService.getAbsolutePath(fileInfo), fileInfo.getName(), fileInfo.getContent());

                if (copyResult.isFailure()) {
                    throw new InternalRuntimeException(getJobName() + " fail to copy FileInfo(" + fileInfoView.getFileNo() + ") to Nginx Service.");
                }
            } else {
                // 删除操作
                // 尝试删除物理文件
                fileService.deleteFileInHardDisk(fileInfoView);
            }
        }

        return result;
    }

    @Override
    protected void batchCompleteHook(HyggeBlogJpaContext<FileInfoView> context, HyggeJobBatchItem<RefreshFileJobItem<FileInfo>> jobBatchItem, List<FileInfoView> rawDataList, List<FileInfo> processedDataList) {
        List<FileInfoView> publicFileViewList = rawDataList.stream().
                filter(it -> PermissionServiceImpl._PUBLIC.getPermissionId().equals(it.getPermissionId()))
                .toList();

        int expectTotalAffected = publicFileViewList.size();
        int totalAffected;

        if (context.getFileObject(RefreshFileJobKey.IS_UPDATE_MODE)) {
            // 更新操作
            totalAffected = fileService.updateFileCacheLink(publicFileViewList);
            if (totalAffected != expectTotalAffected) {
                throw new InternalRuntimeException(getJobName() + " fail to update file links, totalAffected(expected:" + publicFileViewList.size() + " but: " + totalAffected + ") in batch(" + jobBatchItem.getBatchCount() + ").");
            }
        } else {
            // 删除操作
            List<String> fileNoList = publicFileViewList.stream()
                    .filter(Objects::nonNull)
                    .map(FileInfoBase::getFileNo)
                    .toList();

            // 批量删除数据库中旧缓存链接并置为无缓存状态
            totalAffected = fileInfoDao.removeFileCacheLinkMultiple(fileNoList);
            if (totalAffected != expectTotalAffected) {
                throw new InternalRuntimeException(getJobName() + " fail to remove file links, totalAffected(expected:" + publicFileViewList.size() + " but: " + totalAffected + ") in batch(" + jobBatchItem.getBatchCount() + ").");
            }
        }

        super.batchCompleteHook(context, jobBatchItem, rawDataList, processedDataList);
    }

    @Override
    protected void finallyHook(HyggeBlogJpaContext<FileInfoView> context) {
        if (context.isSuccess()) {
            // 图片缓存变更后，图片链接缓存也得更新（以清空代替更新）
            cacheService.clearCacheByType(CacheObjectContainer.CacheTypeEnum.FILE_NO_URL_MAPPING);
            context.getJobReporter().addProcessTrackingInfo(System.currentTimeMillis(), "清空了全部 FILE_NO_URL_MAPPING 缓存。");

            // 更新所有博文、句子搜藏 ES 缓存
            eventService.refreshArticleForAll(false);
            context.getJobReporter().addProcessTrackingInfo(System.currentTimeMillis(), "更新了全部 博文 ES 缓存。");
            eventService.refreshQuoteForAll(false);
            context.getJobReporter().addProcessTrackingInfo(System.currentTimeMillis(), "更新了全部 句子收藏 ES 缓存。");
        }
    }
}
