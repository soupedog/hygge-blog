package hygge.blog.domain.dto;

import hygge.blog.domain.enums.ArticleStateEnum;
import hygge.blog.domain.enums.QuoteStateEnum;
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
}
