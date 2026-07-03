package hygge.blog.controller;

import hygge.blog.common.annotation.RequireAuth;
import hygge.blog.controller.doc.ManageControllerDoc;
import hygge.blog.domain.local.bo.CacheObjectContainer;
import hygge.blog.domain.local.bo.HyggeBlogControllerResponse;
import hygge.blog.service.elasticsearch.ElasticSearchServiceImpl;
import hygge.blog.service.local.CacheServiceImpl;
import hygge.blog.service.local.EventServiceImpl;
import hygge.blog.service.local.FileCacheRefreshServiceImpl;
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
    private final ElasticSearchServiceImpl elasticSearchService;
    private final CacheServiceImpl cacheService;
    private final EventServiceImpl eventService;

    public ManageController(FileCacheRefreshServiceImpl fileCacheRefreshService, ElasticSearchServiceImpl elasticSearchService, CacheServiceImpl cacheService, EventServiceImpl eventService) {
        this.fileCacheRefreshService = fileCacheRefreshService;
        this.elasticSearchService = elasticSearchService;
        this.cacheService = cacheService;
        this.eventService = eventService;
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
    @PostMapping(value = "/refresh/fileCache")
    public ResponseEntity<HyggeBlogControllerResponse<String>> refreshPublicFileCache(@RequestParam(required = false, defaultValue = "false") Boolean forceOverWrite) {
        fileCacheRefreshService.freshAllPublicFileCache(forceOverWrite, true);
        // 更新完图片资源需要刷新缓存
        cacheService.clearCacheByType(CacheObjectContainer.CacheTypeEnum.FILE_NO_URL_MAPPING);
        eventService.refreshArticleForAll(false);
        elasticSearchService.freshAllQuote();
        return (ResponseEntity<HyggeBlogControllerResponse<String>>) success("更新完毕:" + new Timestamp(System.currentTimeMillis()));
    }

    @Override
    @RequireAuth
    @DeleteMapping(value = "/refresh/fileCache")
    public ResponseEntity<HyggeBlogControllerResponse<String>> removePublicFileCache() {
        fileCacheRefreshService.freshAllPublicFileCache(true, false);
        // 更新完图片资源需要刷新缓存
        cacheService.clearCacheByType(CacheObjectContainer.CacheTypeEnum.FILE_NO_URL_MAPPING);
        eventService.refreshArticleForAll(false);
        eventService.refreshQuoteForAll(false);
        return (ResponseEntity<HyggeBlogControllerResponse<String>>) success("更新完毕:" + new Timestamp(System.currentTimeMillis()));
    }
}
