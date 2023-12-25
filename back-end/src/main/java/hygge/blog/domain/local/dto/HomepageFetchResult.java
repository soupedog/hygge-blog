package hygge.blog.domain.local.dto;

import hygge.blog.domain.local.dto.inner.ArticleSummaryInfo;
import hygge.blog.domain.local.dto.inner.TopicOverviewInfo;
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
 * @date 2022/7/25
 */
@Getter
@Setter
@Builder
@Generated
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "首页初始化信息", description = "首页初始化所需要的信息")
public class HomepageFetchResult {
    private List<TopicOverviewInfo> topicOverviewInfoList;
    private ArticleSummaryInfo articleSummaryInfo;
    private List<AnnouncementDto> announcementInfoList;
}
