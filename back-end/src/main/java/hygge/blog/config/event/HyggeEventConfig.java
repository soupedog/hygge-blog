package hygge.blog.config.event;

import hygge.blog.event.listener.ESRefreshListener;
import hygge.blog.service.elasticsearch.RefreshElasticSearchServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Xavier
 * @date 2025/9/1
 */
@Configuration
public class HyggeEventConfig {

    @Bean
    public ESRefreshListener esRefreshListener(RefreshElasticSearchServiceImpl refreshElasticSearchService) {
        return new ESRefreshListener(refreshElasticSearchService);
    }
}
