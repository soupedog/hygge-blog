package hygge.blog.service.local;

import hygge.blog.domain.local.dto.ArticleQuoteSearchCache;
import hygge.blog.event.ESRefreshEvent;
import hygge.blog.event.ESRefreshEventInfo;
import hygge.blog.event.base.HyggeEvent;
import hygge.blog.event.listener.base.HyggeEventListener;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/**
 * event 具体处理逻辑见 {@link HyggeEventListener} 的具体实现类
 *
 * @author Xavier
 * @date 2025/9/1
 */
@Service
public class EventServiceImpl {
    private final ApplicationEventPublisher applicationEventPublisher;

    public EventServiceImpl(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void refreshArticleByArticleId(Integer articleId) {
        ESRefreshEvent event = new ESRefreshEvent(
                new ESRefreshEventInfo(ArticleQuoteSearchCache.Type.ARTICLE, false, articleId)
        );
        fireEvent(event);
    }

    public void refreshQuoteByQuoteId(Integer quoteId) {
        ESRefreshEvent event = new ESRefreshEvent(
                new ESRefreshEventInfo(ArticleQuoteSearchCache.Type.QUOTE, false, quoteId)
        );
        fireEvent(event);
    }

    public void fireEvent(HyggeEvent<?> event) {
        applicationEventPublisher.publishEvent(event);
    }
}