package hygge.blog.controller.doc;

import hygge.blog.controller.base.HyggeBlogController;
import hygge.blog.domain.local.bo.HyggeBlogControllerResponse;
import hygge.blog.domain.local.enums.ResourceLinkRefreshTypeEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

/**
 * @author Xavier
 * @date 2026/5/21
 */
@Tag(name = "管理操作 Controller", description = "博客站管理员专属操作")
public interface ManageControllerDoc extends HyggeBlogController<ResponseEntity<?>> {
    @Operation(summary = "全部文章图片链接更新", description = "更新文章图片链接")
    ResponseEntity<HyggeBlogControllerResponse<String>> refreshImageLink(ResourceLinkRefreshTypeEnum resourceLinkRefreshType);
}
