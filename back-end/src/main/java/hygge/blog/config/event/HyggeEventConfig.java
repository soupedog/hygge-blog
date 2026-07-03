package hygge.blog.config.event;

import hygge.blog.event.listener.ESRefreshListener;
import hygge.blog.event.listener.FileCacheRefreshListener;
import hygge.blog.job.RefreshArticleJob;
import hygge.blog.service.elasticsearch.ElasticSearchServiceImpl;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Xavier
 * @date 2025/9/1
 */
@Configuration
public class HyggeEventConfig {
    @Bean
    public ESRefreshListener esRefreshListener(ElasticSearchServiceImpl elasticSearchService, RefreshArticleJob refreshArticleJob) {
        return new ESRefreshListener(elasticSearchService, refreshArticleJob);
    }

    @Bean
    public FileCacheRefreshListener fileCacheRefreshListener(CacheManager cacheManager) {
        return new FileCacheRefreshListener(cacheManager);
    }
}
