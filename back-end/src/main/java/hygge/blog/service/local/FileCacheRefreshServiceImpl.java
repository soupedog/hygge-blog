package hygge.blog.service.local;

import hygge.blog.domain.local.po.view.FileInfoView;
import hygge.blog.repository.database.FileInfoViewDao;
import hygge.blog.service.local.inner.file.FileUrlBuilder;
import hygge.blog.service.local.normal.PermissionServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
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
    private final FileUrlBuilder fileUrlBuilder;
    private final FileInfoViewDao fileInfoViewDao;

    @Autowired
    public FileCacheRefreshServiceImpl(FileServiceImpl fileService, FileUrlBuilder fileUrlBuilder, FileInfoViewDao fileInfoViewDao) {
        this.fileService = fileService;
        this.fileUrlBuilder = fileUrlBuilder;
        this.fileInfoViewDao = fileInfoViewDao;
    }

    public void freshAllPublicFileCache(boolean forceOverWrite) {
        // 尝试获取执行权（false -> true）
        if (!conflictFlag.compareAndSet(false, true)) {
            throw new IllegalStateException("冲突，请等待未执行完的任务执行完");
        }

        try {
            long startTs = System.currentTimeMillis();
            AtomicInteger totalCount = new AtomicInteger(0);

            Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.asc("fileId")));

            Example<FileInfoView> example = Example.of(FileInfoView.builder()
                    .permissionId(PermissionServiceImpl._PUBLIC.getPermissionId())
                    .build()
            );

            Page<FileInfoView> fileInfoViewPage = fileInfoViewDao.findAll(example, pageable);

            List<FileInfoView> fileInfoViewList = null;
            do {
                if (fileInfoViewList != null) {
                    fileInfoViewPage = fileInfoViewDao.findAll(fileInfoViewPage.nextPageable());
                }
                fileInfoViewList = fileInfoViewPage.getContent();

                fileInfoViewList.forEach(fileInfoView -> {
                    freshSingleFile(forceOverWrite, totalCount, fileInfoView);
                });
            } while (!fileInfoViewPage.isLast());

            log.info("已刷新公共可见文件数 {} 耗时 {} ms", totalCount.get(), System.currentTimeMillis() - startTs);
        } finally {
            // 执行结束，释放标识
            conflictFlag.set(false);
        }
    }

    public void freshSingleFile(boolean forceOverWrite, AtomicInteger totalCount, FileInfoView fileInfoView) {
        if (fileService.createFileCopyFromDBToHardDisk(forceOverWrite, fileInfoView.getFileNo())) {
            totalCount.incrementAndGet();
        }
    }
}
