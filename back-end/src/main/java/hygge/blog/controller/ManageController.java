package hygge.blog.controller;

import hygge.blog.common.annotation.RequireAuth;
import hygge.blog.controller.doc.ManageControllerDoc;
import hygge.blog.domain.local.bo.CacheObjectContainer;
import hygge.blog.domain.local.bo.HyggeBlogControllerResponse;
import hygge.blog.service.elasticsearch.RefreshElasticSearchServiceImpl;
import hygge.blog.service.local.CacheServiceImpl;
import hygge.blog.service.local.FileCacheRefreshServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
    private final FileCacheRefreshServiceImpl fileCacheRefreshService;
    private final RefreshElasticSearchServiceImpl refreshElasticSearchService;
    private final CacheServiceImpl cacheService;

    @Autowired
    public ManageController(FileCacheRefreshServiceImpl fileCacheRefreshService, RefreshElasticSearchServiceImpl refreshElasticSearchService, CacheServiceImpl cacheService) {
        this.fileCacheRefreshService = fileCacheRefreshService;
        this.refreshElasticSearchService = refreshElasticSearchService;
        this.cacheService = cacheService;
    }

    @Override
    @RequireAuth
    @PostMapping(value = "/refresh/fileCache")
    public ResponseEntity<HyggeBlogControllerResponse<String>> refreshPublicFileCache(@RequestParam(required = false, defaultValue = "false") Boolean forceOverWrite) {
        fileCacheRefreshService.freshAllPublicFileCache(forceOverWrite, true);
        // 更新完图片资源需要刷新缓存
        cacheService.clearCache(CacheObjectContainer.CacheTypeEnum.FILE_NO_URL_MAPPING);
        refreshElasticSearchService.freshAllArticle();
        refreshElasticSearchService.freshAllQuote();
        return (ResponseEntity<HyggeBlogControllerResponse<String>>) success("更新完毕:" + new Timestamp(System.currentTimeMillis()));
    }

    @Override
    @RequireAuth
    @DeleteMapping(value = "/refresh/fileCache")
    public ResponseEntity<HyggeBlogControllerResponse<String>> removePublicFileCache() {
        fileCacheRefreshService.freshAllPublicFileCache(true, false);
        // 更新完图片资源需要刷新缓存
        cacheService.clearCache(CacheObjectContainer.CacheTypeEnum.FILE_NO_URL_MAPPING);
        refreshElasticSearchService.freshAllArticle();
        refreshElasticSearchService.freshAllQuote();
        return (ResponseEntity<HyggeBlogControllerResponse<String>>) success("更新完毕:" + new Timestamp(System.currentTimeMillis()));
    }
}
