package hygge.blog.domain.dto.inner;

import hygge.blog.domain.dto.ArticleDto;
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
 * @date 2022/7/27
 */
@Getter
@Setter
@Builder
@Generated
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "文章摘要信息")
public class ArticleSummaryInfo {
    @Schema(title = "文章摘要信息数组")
    private List<ArticleDto> articleSummaryList;
    @Schema(title = "文章总数")
    private int totalCount;
}
