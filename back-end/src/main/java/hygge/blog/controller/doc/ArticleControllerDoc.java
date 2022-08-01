package hygge.blog.controller.doc;

import hygge.blog.controller.base.HyggeBlogController;
import hygge.blog.domain.bo.HyggeBlogControllerResponse;
import hygge.blog.domain.dto.ArticleDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.Map;

/**
 * @author Xavier
 * @date 2022/7/20
 */
@Tag(name = "文章 Controller", description = "文章的创建修改操作")
public interface ArticleControllerDoc extends HyggeBlogController<ResponseEntity<?>> {
    @Operation(summary = "创建文章", description = "使用当前登录用户创建一个新的文章")
    ResponseEntity<HyggeBlogControllerResponse<ArticleDto>> createArticle(ArticleDto articleDto);

    @Operation(summary = "修改文章", description = "持有人修改文章信息")
    @RequestBody(
            content = {@Content(schema =
            @Schema(ref = "#/components/schemas/ArticleDto")
            )}
    )
    ResponseEntity<HyggeBlogControllerResponse<ArticleDto>> updateArticle(String aid, Map<String, Object> data);

    @Operation(summary = "查询文章", description = "查询文章详情")
    ResponseEntity<HyggeBlogControllerResponse<ArticleDto>> findArticle(String aid);
}
