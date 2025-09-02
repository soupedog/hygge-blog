package hygge.blog.service.local;

import hygge.blog.domain.local.dto.ArticleQuoteSearchCache;
import hygge.blog.event.ESRefreshEvent;
import hygge.blog.event.ESRefreshEventInfo;
import hygge.blog.event.base.HyggeEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/**
 * @author Xavier
 * @date 2025/9/1
 */
@Service
public class EventServiceImpl {
    private final ApplicationEventPublisher applicationEventPublisher;

    public EventServiceImpl(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void refreshArticleByArticleIdAsync(Integer articleId) {
        ESRefreshEvent event = new ESRefreshEvent(
                new ESRefreshEventInfo(ArticleQuoteSearchCache.Type.ARTICLE, false, articleId)
        );
        publishEvent(event);
    }

    public void refreshQuoteByQuoteIdAsync(Integer quoteId) {
        ESRefreshEvent event = new ESRefreshEvent(
                new ESRefreshEventInfo(ArticleQuoteSearchCache.Type.QUOTE, false, quoteId)
        );
        publishEvent(event);
    }

    public void publishEvent(HyggeEvent<?> event) {
        applicationEventPublisher.publishEvent(event);
    }
}