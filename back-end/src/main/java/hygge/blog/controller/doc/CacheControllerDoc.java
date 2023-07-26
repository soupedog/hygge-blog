package hygge.blog.controller.doc;

import hygge.blog.controller.base.HyggeBlogController;
import hygge.blog.domain.local.bo.HyggeBlogControllerResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

/**
 * @author Xavier
 * @date 2023/7/26
 */
@Tag(name = "缓存 Controller", description = "缓存的相关操作")
public interface CacheControllerDoc extends HyggeBlogController<ResponseEntity<?>> {
    @Operation(summary = "清空特定缓存", description = "主动清空目标缓存内容")
    ResponseEntity<HyggeBlogControllerResponse<Void>> clearCache(String cacheName);
}
