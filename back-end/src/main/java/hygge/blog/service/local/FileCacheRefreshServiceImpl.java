package hygge.blog.service.local;

import hygge.blog.domain.local.po.view.FileInfoView;
import hygge.blog.repository.database.FileInfoDao;
import hygge.blog.repository.database.FileInfoViewDao;
import hygge.blog.service.local.normal.PermissionServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Xavier
 * @date 2026/5/27
 */
@Slf4j
@Service
public class FileCacheRefreshServiceImpl {
    private static final AtomicBoolean conflictFlag = new AtomicBoolean(false);

    private final FileServiceImpl fileService;
    private final FileInfoViewDao fileInfoViewDao;
    private final FileInfoDao fileInfoDao;
    private final EventServiceImpl eventService;

    public FileCacheRefreshServiceImpl(FileServiceImpl fileService, FileInfoViewDao fileInfoViewDao, FileInfoDao fileInfoDao, EventServiceImpl eventService) {
        this.fileService = fileService;
        this.fileInfoViewDao = fileInfoViewDao;
        this.fileInfoDao = fileInfoDao;
        this.eventService = eventService;
    }

    public void freshAllPublicFileCache(boolean forceOverWrite, boolean isAdd) {
        // 尝试获取执行权（false -> true）
        if (!conflictFlag.compareAndSet(false, true)) {
            throw new IllegalStateException("冲突，请等待未执行完的任务执行完");
        }

        try {
            long startTs = System.currentTimeMillis();
            AtomicInteger totalCount = new AtomicInteger(0);

            Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.asc("fileId")));

            Page<FileInfoView> fileInfoViewPage = fileInfoViewDao.findAll(pageable);

            List<FileInfoView> fileInfoViewList = null;
            do {
                if (fileInfoViewList != null) {
                    fileInfoViewPage = fileInfoViewDao.findAll(fileInfoViewPage.nextPageable());
                }
                fileInfoViewList = fileInfoViewPage.getContent();

                fileInfoViewList.forEach(fileInfoView -> {
                    if (PermissionServiceImpl._PUBLIC.getPermissionId().equals(fileInfoView.getPermissionId())) {
                        // 只处理公开可见类型
                        if (isAdd) {
                            freshSingleFile_add(forceOverWrite, totalCount, fileInfoView);
                        } else {
                            freshSingleFile_remove(totalCount, fileInfoView);
                        }
                    }
                });
            } while (!fileInfoViewPage.isLast());

            log.info("已刷新公共可见文件数 {} 耗时 {} ms", totalCount.get(), System.currentTimeMillis() - startTs);
        } finally {
            // 执行结束，释放标识
            conflictFlag.set(false);
        }
    }

    public void freshSingleFile_add(boolean forceOverWrite, AtomicInteger totalCount, FileInfoView fileInfoView) {
        if (fileService.createFileCopyFromDBToHardDisk(forceOverWrite, fileInfoView.getFileNo())) {
            totalCount.incrementAndGet();
        }
    }

    public void freshSingleFile_remove(AtomicInteger totalCount, FileInfoView fileInfoView) {
        // 尝试删除物理文件
        fileService.deleteFileInHardDisk(fileInfoView);
        // 删除数据库中旧缓存链接并置为无缓存状态
        fileInfoDao.removeFileCacheLink(fileInfoView.getFileNo());
        // 删除链接查询缓存
        eventService.refreshFileCacheLinkByFileNo(fileInfoView.getFileNo());
        totalCount.incrementAndGet();
    }
}
