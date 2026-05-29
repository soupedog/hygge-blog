package hygge.blog.controller;

import hygge.blog.common.annotation.RequireAuth;
import hygge.blog.controller.doc.CacheControllerDoc;
import hygge.blog.domain.local.bo.CacheObjectContainer;
import hygge.blog.domain.local.bo.HyggeBlogControllerResponse;
import hygge.blog.service.local.CacheServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Xavier
 * @date 2023/7/26
 */
@RestController
@RequestMapping(value = "/blog-service/api/main")
public class CacheController implements CacheControllerDoc {
    private final CacheServiceImpl cacheService;

    @Autowired
    public CacheController(CacheServiceImpl cacheService) {
        this.cacheService = cacheService;
    }

    @Override
    @RequireAuth
    @DeleteMapping("/cache")
    public ResponseEntity<HyggeBlogControllerResponse<Void>> clearCache(@RequestParam(value = "cacheType", defaultValue = "CATEGORY_TREE") CacheObjectContainer.CacheTypeEnum cacheType) {
        cacheService.clearCache(cacheType);
        return (ResponseEntity<HyggeBlogControllerResponse<Void>>) success();
    }
}
