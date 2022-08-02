package hygge.blog.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * @author Xavier
 * @date 2022/8/3
 */
@Getter
@Setter
@Builder
@Generated
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "句子收藏信息")
public class QuoteInfo {
    @Schema(title = "句子收藏信息数组")
    private List<QuoteDto> quoteList;
    @Schema(title = "句子收藏总数")
    private int totalCount;
}
