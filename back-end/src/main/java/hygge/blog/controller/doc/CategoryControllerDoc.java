package hygge.blog.controller.doc;

import hygge.blog.controller.base.HyggeBlogController;
import hygge.blog.domain.local.bo.HyggeBlogControllerResponse;
import hygge.blog.domain.local.dto.CategoryDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.Map;

/**
 * @author Xavier
 * @date 2022/7/23
 */
@Tag(name = "文章类别 Controller", description = "文章类别的创建修改操作")
public interface CategoryControllerDoc extends HyggeBlogController<ResponseEntity<?>> {
    @Operation(summary = "创建文章类别", description = "使用当前登录用户创建一个新的文章类别")
    ResponseEntity<HyggeBlogControllerResponse<CategoryDto>> createCategory(CategoryDto topicDto);

    @Operation(summary = "修改文章类别", description = "群持有人修改文章类别信息")
    @RequestBody(
            content = {@Content(schema =
            @Schema(ref = "#/components/schemas/CategoryDto")
            )}
    )
    ResponseEntity<HyggeBlogControllerResponse<CategoryDto>> updateCategory(String cid, Map<String, Object> data);
}
