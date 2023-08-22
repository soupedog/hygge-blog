package hygge.blog.config.backgroundjob;

import hygge.blog.service.elasticsearch.RefreshElasticSearchServiceImpl;
import hygge.blog.service.local.normal.ArticleBrowseLogServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @author Xavier
 * @date 2022/8/29
 */
@Profile("!dev")
@Configuration
public class BackgroundJobConfig {
    @Autowired
    private RefreshElasticSearchServiceImpl refreshElasticSearchService;
    @Autowired
    private ArticleBrowseLogServiceImpl articleBrowseLogService;

    @Scheduled(fixedDelay = 1000 * 3600)
    public void toFreshArticleSearchData() {
        refreshElasticSearchService.freshAllArticle();
    }

    @Scheduled(fixedDelay = 1000 * 3600, initialDelay = 1000 * 1800)
    public void toFreshQuoteSearchData() {
        refreshElasticSearchService.freshQuote();
    }

    /**
     * ip 查询接口是第三方提供的公共服务，频率限制比想象中更严格，每 8 小时调用一次
     */
    @Scheduled(fixedDelay = 1000 * 3600 * 8, initialDelay = 1000 * 300)
    public void toFreshIpLocationData() {
        articleBrowseLogService.freshIpLocationBackgroundJob();
    }
}
