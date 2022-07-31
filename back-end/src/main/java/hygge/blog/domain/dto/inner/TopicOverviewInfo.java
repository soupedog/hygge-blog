package hygge.blog.domain.dto.inner;

import hygge.blog.domain.dto.ArticleDto;
import hygge.blog.domain.dto.CategoryDto;
import hygge.blog.domain.dto.TopicDto;
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
 * @date 2022/7/24
 */
@Getter
@Setter
@Builder
@Generated
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "板块概览信息", description = "描述板块下文章类别基本信息")
public class TopicOverviewInfo {
    @Schema(title = "板块信息")
    private TopicDto topicInfo;
    @Schema(title = "板块下所有文章类别容器")
    private List<CategoryDto> categoryListInfo;
    @Schema(title = "板块下文章摘要信息")
    private List<ArticleDto> articleSummaryList;
    @Schema(title = "板块下文章总数")
    private int totalCount;
}
