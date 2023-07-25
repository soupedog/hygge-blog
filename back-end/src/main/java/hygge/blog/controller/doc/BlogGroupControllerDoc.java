package hygge.blog.controller.doc;

import hygge.blog.controller.base.HyggeBlogController;
import hygge.blog.domain.local.bo.HyggeBlogControllerResponse;
import hygge.blog.domain.local.dto.BlogGroupDto;
import hygge.blog.domain.local.dto.GroupBindInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

/**
 * @author Xavier
 * @date 2022/7/20
 */
@Tag(name = "博客群组 Controller", description = "群组创建出入组操作")
public interface BlogGroupControllerDoc extends HyggeBlogController<ResponseEntity<?>> {
    @Operation(summary = "创建群组", description = "使用当前登录用户创建一个新的群组")
    ResponseEntity<HyggeBlogControllerResponse<BlogGroupDto>> createBlogGroup(BlogGroupDto blogGroupDto);

    @Operation(summary = "用户入组", description = "群持有人令目标用户入组")
    ResponseEntity<HyggeBlogControllerResponse<Void>> groupAdmission(GroupBindInfo groupBindInfo);

    @Operation(summary = "用户出组", description = "群持有人令目标用户出组")
    ResponseEntity<HyggeBlogControllerResponse<Void>> groupEviction(GroupBindInfo groupBindInfo);

}
