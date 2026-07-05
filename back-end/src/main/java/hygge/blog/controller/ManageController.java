package hygge.blog.controller;

import hygge.blog.common.annotation.RequireAuth;
import hygge.blog.controller.doc.ManageControllerDoc;
import hygge.blog.domain.local.bo.CacheObjectContainer;
import hygge.blog.domain.local.bo.HyggeBlogControllerResponse;
import hygge.blog.domain.local.po.view.FileInfoView;
import hygge.blog.job.RefreshFileCacheJob;
import hygge.blog.job.key.RefreshFileJobKey;
import hygge.blog.job.other.HyggeBlogJpaContext;
import hygge.blog.service.local.CacheServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;

/**
 * @author Xavier
 * @date 2026/5/21
 */
@RestController
@RequestMapping(value = "/blog-service/api/main")
public class ManageController implements ManageControllerDoc {
    private final CacheServiceImpl cacheService;
    private final RefreshFileCacheJob refreshFileCacheJob;

    public ManageController(CacheServiceImpl cacheService, RefreshFileCacheJob refreshFileCacheJob) {
        this.cacheService = cacheService;
        this.refreshFileCacheJob = refreshFileCacheJob;
    }

    @Override
    @RequireAuth
    @DeleteMapping("/cache")
    public ResponseEntity<HyggeBlogControllerResponse<Void>> clearCache(@RequestParam(value = "cacheType", defaultValue = "CATEGORY_TREE") CacheObjectContainer.CacheTypeEnum cacheType) {
        cacheService.clearCacheByType(cacheType);
        return (ResponseEntity<HyggeBlogControllerResponse<Void>>) success();
    }

    @Override
    @RequireAuth
    @PutMapping(value = "/refresh/fileCache")
    public ResponseEntity<HyggeBlogControllerResponse<String>> refreshPublicFileCache(@RequestParam(required = false, defaultValue = "false") Boolean isAddMode) {
        HyggeBlogJpaContext<FileInfoView> context = new HyggeBlogJpaContext<>("更新全部公开文件 Nginx 缓存", 25, false);
        context.saveFileObject(RefreshFileJobKey.IS_UPDATE_MODE, isAddMode);
        refreshFileCacheJob.execute(context);
        return (ResponseEntity<HyggeBlogControllerResponse<String>>) success("更新完毕:" + new Timestamp(System.currentTimeMillis()));
    }
}
