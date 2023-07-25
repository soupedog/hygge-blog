package hygge.blog.controller.doc;

import hygge.blog.controller.base.HyggeBlogController;
import hygge.blog.domain.local.bo.HyggeBlogControllerResponse;
import hygge.blog.domain.local.dto.TopicDto;
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
@Tag(name = "板块 Controller", description = "板块的创建修改操作")
public interface TopicControllerDoc extends HyggeBlogController<ResponseEntity<?>> {
    @Operation(summary = "创建板块", description = "使用当前登录用户创建一个新的板块")
    ResponseEntity<HyggeBlogControllerResponse<TopicDto>> createTopic(TopicDto topicDto);

    @Operation(summary = "修改板块", description = "群持有人修改板块信息")
    @RequestBody(
            content = {@Content(schema =
            @Schema(ref = "#/components/schemas/TopicDto")
            )}
    )
    ResponseEntity<HyggeBlogControllerResponse<TopicDto>> updateTopic(String tid, Map<String, Object> data);
}
