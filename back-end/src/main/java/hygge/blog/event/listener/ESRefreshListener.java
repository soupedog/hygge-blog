package hygge.blog.event.listener;

import hygge.blog.domain.local.dto.ArticleQuoteSearchCache;
import hygge.blog.event.ESRefreshEvent;
import hygge.blog.event.ESRefreshEventInfo;
import hygge.blog.service.elasticsearch.RefreshElasticSearchServiceImpl;
import hygge.commons.spring.event.BaseHyggeEventListener;
import hygge.commons.spring.event.HyggeEventListenerContext;

/**
 * @author Xavier
 * @date 2025/9/1
 */
public class ESRefreshListener extends BaseHyggeEventListener<ESRefreshEventInfo, ESRefreshEvent> {
    private final RefreshElasticSearchServiceImpl refreshElasticSearchService;

    public ESRefreshListener(RefreshElasticSearchServiceImpl refreshElasticSearchService) {
        this.refreshElasticSearchService = refreshElasticSearchService;
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
                refreshElasticSearchService.freshAllQuote();
            } else if (ArticleQuoteSearchCache.Type.ARTICLE.equals(info.getType())) {
                refreshElasticSearchService.freshAllArticle();
            }
        } else {
            // 单个刷新
            if (ArticleQuoteSearchCache.Type.QUOTE.equals(info.getType())) {
                refreshElasticSearchService.freshSingleQuote(info.getQuoteId());
            } else if (ArticleQuoteSearchCache.Type.ARTICLE.equals(info.getType())) {
                refreshElasticSearchService.freshSingleArticle(info.getArticleId());
            }
        }
    }
}
