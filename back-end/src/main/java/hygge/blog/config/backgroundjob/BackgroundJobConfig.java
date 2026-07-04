package hygge.blog.config.backgroundjob;

import hygge.blog.service.elasticsearch.ElasticSearchServiceImpl;
import hygge.blog.service.local.EventServiceImpl;
import hygge.blog.service.local.normal.ArticleBrowseLogServiceImpl;
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
    private final ElasticSearchServiceImpl searchService;
    private final EventServiceImpl eventService;
    private final ArticleBrowseLogServiceImpl articleBrowseLogService;

    public BackgroundJobConfig(ElasticSearchServiceImpl searchService, EventServiceImpl eventService, ArticleBrowseLogServiceImpl articleBrowseLogService) {
        this.searchService = searchService;
        this.eventService = eventService;
        this.articleBrowseLogService = articleBrowseLogService;
    }

    @Scheduled(fixedDelay = 1000 * 3600)
    public void toFreshArticleSearchData() {
        eventService.refreshArticleForAll(false);
    }

    @Scheduled(fixedDelay = 1000 * 3600)
    public void toFreshQuoteSearchData() {
        searchService.freshAllQuote();
    }

    // 每 5 分钟一次，初始静默 5 分钟
    @Scheduled(fixedDelay = 1000 * 300, initialDelay = 1000 * 300)
    public void toFreshBrowseLogType() {
        articleBrowseLogService.freshBrowseLogTypeBackgroundJob();
    }

    /**
     * ip 查询接口是第三方提供的公共服务
     */
    @Scheduled(fixedDelay = 1000 * 3600 * 8, initialDelay = 1000 * 300)
    public void toFreshIpLocationData() {
        articleBrowseLogService.freshIpLocationBackgroundJob();
    }
}
