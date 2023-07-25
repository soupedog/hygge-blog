package hygge.blog.controller.doc;

import hygge.blog.controller.base.HyggeBlogController;
import hygge.blog.domain.local.bo.HyggeBlogControllerResponse;
import hygge.blog.domain.local.dto.QuoteDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.Map;

/**
 * @author Xavier
 * @date 2022/8/1
 */
@Tag(name = "句子收藏 Controller", description = "句子收藏的创建修改操作")
public interface QuoteControllerDoc extends HyggeBlogController<ResponseEntity<?>> {
    @Operation(summary = "创建句子收藏", description = "使用当前登录用户创建一个新的句子收藏")
    ResponseEntity<HyggeBlogControllerResponse<QuoteDto>> createQuote(QuoteDto quoteDto);

    @Operation(summary = "修改句子收藏", description = "持有人修改句子收藏信息")
    @RequestBody(
            content = {@Content(schema =
            @Schema(ref = "#/components/schemas/QuoteDto")
            )}
    )
    ResponseEntity<HyggeBlogControllerResponse<QuoteDto>> updateQuote(Integer quoteId, Map<String, Object> data);

    @Operation(summary = "查询句子收藏", description = "查询句子收藏详情")
    ResponseEntity<HyggeBlogControllerResponse<QuoteDto>> findQuote(Integer quoteId);
}
