package hygge.blog.service.local;

import hygge.blog.domain.local.dto.ArticleQuoteSearchCache;
import hygge.blog.event.ESRefreshEvent;
import hygge.blog.event.ESRefreshEventInfo;
import hygge.blog.event.FileCacheRefreshEvent;
import hygge.blog.event.FileCacheRefreshEventInfo;
import hygge.commons.spring.event.BaseHyggeEventListener;
import hygge.commons.spring.event.BaseHyggeEventService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/**
 * event 具体处理逻辑见 {@link BaseHyggeEventListener} 的具体实现类
 *
 * @author Xavier
 * @date 2025/9/1
 */
@Service
public class EventServiceImpl extends BaseHyggeEventService {
    protected EventServiceImpl(ApplicationEventPublisher applicationEventPublisher) {
        super(applicationEventPublisher);
    }

    public void refreshArticleByArticleId(boolean isAsynchronous, Integer articleId) {
        ESRefreshEventInfo source = new ESRefreshEventInfo(ArticleQuoteSearchCache.Type.ARTICLE, false, articleId);

        ESRefreshEvent event = buildEvent(source, ESRefreshEvent::new)
                .asynchronous(isAsynchronous)
                .build();

        fireEvent(event);
    }

    public void refreshArticleForAll(boolean isAsynchronous) {
        ESRefreshEventInfo source = new ESRefreshEventInfo(ArticleQuoteSearchCache.Type.ARTICLE, true, null);

        ESRefreshEvent event = buildEvent(source, ESRefreshEvent::new)
                .asynchronous(isAsynchronous)
                .build();

        fireEvent(event);
    }

    public void refreshFileCacheLinkByFileNo(boolean isAsynchronous, String fileNo) {
        FileCacheRefreshEventInfo source = new FileCacheRefreshEventInfo(fileNo);

        FileCacheRefreshEvent event = buildEvent(source, FileCacheRefreshEvent::new)
                .asynchronous(isAsynchronous)
                .build();

        fireEvent(event);
    }

    public void refreshQuoteByQuoteId(boolean isAsynchronous, Integer quoteId) {
        ESRefreshEventInfo source = new ESRefreshEventInfo(ArticleQuoteSearchCache.Type.QUOTE, false, quoteId);

        ESRefreshEvent event = buildEvent(source, ESRefreshEvent::new)
                .asynchronous(isAsynchronous)
                .build();

        fireEvent(event);
    }

    public void refreshQuoteForAll(boolean isAsynchronous) {
        ESRefreshEventInfo source = new ESRefreshEventInfo(ArticleQuoteSearchCache.Type.QUOTE, true, null);

        ESRefreshEvent event = buildEvent(source, ESRefreshEvent::new)
                .asynchronous(isAsynchronous)
                .build();

        fireEvent(event);
    }
}