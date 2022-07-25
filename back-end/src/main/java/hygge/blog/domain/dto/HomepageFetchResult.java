package hygge.blog.domain.dto;

import hygge.blog.domain.dto.inner.TopicOverviewInfo;
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
public class HomepageFetchResult {
    private List<TopicOverviewInfo> topicOverviewInfoList;
}
