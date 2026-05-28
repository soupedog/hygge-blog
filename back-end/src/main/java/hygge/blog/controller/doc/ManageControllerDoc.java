package hygge.blog.controller.doc;

import hygge.blog.controller.base.HyggeBlogController;
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
    @Operation(summary = "全部公开可见文件缓存更新", description = "更新公开可见文件缓存")
    ResponseEntity<HyggeBlogControllerResponse<String>> refreshPublicFileCache(Boolean forceOverWrite);
}
