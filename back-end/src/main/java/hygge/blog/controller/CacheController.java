package hygge.blog.controller;

import hygge.blog.common.HyggeRequestContext;
import hygge.blog.common.HyggeRequestTracker;
import hygge.blog.controller.doc.CacheControllerDoc;
import hygge.blog.domain.local.bo.HyggeBlogControllerResponse;
import hygge.blog.domain.local.enums.UserTypeEnum;
import hygge.blog.domain.local.po.User;
import hygge.blog.service.local.normal.UserServiceImpl;
import hygge.commons.exception.LightRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
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
    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private UserServiceImpl userService;

    @Override
    @DeleteMapping("/cache")
    public ResponseEntity<HyggeBlogControllerResponse<Void>> clearCache(@RequestParam(value = "cacheName", defaultValue = "categoryTreeInfoCache") String cacheName) {
        HyggeRequestContext context = HyggeRequestTracker.getContext();
        User currentUser = context.getCurrentLoginUser();
        userService.checkUserRight(currentUser, UserTypeEnum.ROOT);

        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            throw new LightRuntimeException("Cache(" + cacheName + ") was not found.");
        }

        cache.clear();

        return (ResponseEntity<HyggeBlogControllerResponse<Void>>) success();
    }
}
