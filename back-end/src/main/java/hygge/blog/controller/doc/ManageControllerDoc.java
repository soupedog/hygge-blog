package hygge.blog.controller.doc;

import hygge.blog.controller.base.HyggeBlogController;
import hygge.blog.domain.local.bo.CacheObjectContainer;
import hygge.blog.domain.local.bo.HyggeBlogControllerResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

/**
 * @author Xavier
 * @date 2026/5/21
 */
@Tag(name = "管理操作 Controller", description = "博客站管理员专属操作")
public interface ManageControllerDoc extends HyggeBlogController<ResponseEntity<?>> {
    @Operation(summary = "清空特定缓存", description = "主动清空目标缓存内容")
    ResponseEntity<HyggeBlogControllerResponse<Void>> clearCache(CacheObjectContainer.CacheTypeEnum cacheType);

    @Operation(summary = "更新全部公开可见文件缓存", description = "更新公开可见文件缓存")
    ResponseEntity<HyggeBlogControllerResponse<String>> refreshPublicFileCache(Boolean forceOverWrite);

    @Operation(summary = "移除全部公开可见文件缓存", description = "移除公开可见文件缓存")
    ResponseEntity<HyggeBlogControllerResponse<String>> removePublicFileCache();
}
