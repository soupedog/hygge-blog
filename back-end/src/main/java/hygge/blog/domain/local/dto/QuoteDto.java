package hygge.blog.domain.local.dto;

import hygge.blog.domain.local.enums.QuoteStateEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 句子收藏
 *
 * @author Xavier
 * @date 2022/8/1
 */
@Getter
@Setter
@Builder
@Generated
@NoArgsConstructor
@AllArgsConstructor
public class QuoteDto {
    @Schema(title = "句子 ID", description = "系统自动生成句子 ID")
    private Integer quoteId;
    @Schema(title = "用户编号", description = "系统自动生成用户编号", example = "U00000001")
    private String uid;
    @Schema(title = "主图绝对路径")
    private String imageSrc;
    @Schema(title = "内容")
    private String content;
    @Schema(title = "可能的出处")
    private String source;
    @Schema(title = "传送门")
    private String portal;
    @Schema(title = "备注")
    private String remarks;
    @Schema(title = "排序优先级(越大越靠前)")
    private Integer orderVal;
    @Schema(title = "句子状态", description = "启用,禁用")
    private QuoteStateEnum quoteState;
    @Schema(title = "创建时间", description = "UTC 毫秒级时间戳")
    protected Long createTs;
    @Schema(title = "最后修改时间", description = "UTC 毫秒级时间戳")
    protected Long lastUpdateTs;
}
