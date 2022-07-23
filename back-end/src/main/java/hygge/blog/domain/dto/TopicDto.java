package hygge.blog.domain.dto;


import hygge.blog.domain.enums.TopicStateEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 文章板块
 *
 * @author Xavier
 * @date 2022/7/21
 */
@Getter
@Setter
@Builder
@Generated
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "板块信息")
public class TopicDto {
    @Schema(title = "板块编号", description = "系统自动生成板块编号")
    private String tid;
    @Schema(title = "板块名称", description = "创建者自定义的名称")
    private String topicName;
    @Schema(title = "用户编号", description = "系统自动生成用户编号", example = "U00000001")
    private String uid;
    @Schema(title = "排序优先级", description = "越大越靠前")
    private Integer orderVal;
    @Schema(title = "板块状态", description = "禁用,启用")
    private TopicStateEnum topicState;
}