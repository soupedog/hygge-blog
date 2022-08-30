package hygge.blog.config.backgroundjob;

import hygge.blog.elasticsearch.service.RefreshElasticSearchServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @author Xavier
 * @date 2022/8/29
 */
@Configuration
public class BackgroundJobConfig {
    @Autowired
    private RefreshElasticSearchServiceImpl refreshElasticSearchService;

    @Scheduled(fixedDelay = 1000 * 3600)
    public void toFreshArticleSearchData() {
        refreshElasticSearchService.freshArticle();
    }

    @Scheduled(fixedDelay = 1000 * 3600, initialDelay = 1000 * 1800)
    public void toFreshQuoteSearchData() {
        refreshElasticSearchService.freshQuote();
    }
}
