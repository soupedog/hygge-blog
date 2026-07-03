package hygge.blog.event.listener;

import hygge.blog.domain.local.dto.ArticleQuoteSearchCache;
import hygge.blog.event.ESRefreshEvent;
import hygge.blog.event.ESRefreshEventInfo;
import hygge.blog.job.RefreshArticleJob;
import hygge.blog.service.elasticsearch.ElasticSearchServiceImpl;
import hygge.commons.spring.event.BaseHyggeEventListener;
import hygge.commons.spring.event.HyggeEventListenerContext;

/**
 * @author Xavier
 * @date 2025/9/1
 */
public class ESRefreshListener extends BaseHyggeEventListener<ESRefreshEventInfo, ESRefreshEvent> {
    private final ElasticSearchServiceImpl elasticSearchService;
    private final RefreshArticleJob refreshArticleJob;

    public ESRefreshListener(ElasticSearchServiceImpl elasticSearchService, RefreshArticleJob refreshArticleJob) {
        this.elasticSearchService = elasticSearchService;
        this.refreshArticleJob = refreshArticleJob;
    }

    @Override
    protected String getListenerName() {
        return this.getClass().getSimpleName();
    }

    @Override
    protected void handleEvent(HyggeEventListenerContext<ESRefreshEventInfo, ESRefreshEvent> context, ESRefreshEvent event) {
        ESRefreshEventInfo info = event.getActualSource();

        if (info.isForAll()) {
            // 全量刷新
            if (ArticleQuoteSearchCache.Type.QUOTE.equals(info.getType())) {
                elasticSearchService.freshAllQuote();
            } else if (ArticleQuoteSearchCache.Type.ARTICLE.equals(info.getType())) {
                refreshArticleJob.execute();
            }
        } else {
            // 单个刷新
            if (ArticleQuoteSearchCache.Type.QUOTE.equals(info.getType())) {
                elasticSearchService.freshSingleQuote(info.getQuoteId());
            } else if (ArticleQuoteSearchCache.Type.ARTICLE.equals(info.getType())) {
                elasticSearchService.freshSingleArticle(info.getArticleId());
            }
        }
    }
}
