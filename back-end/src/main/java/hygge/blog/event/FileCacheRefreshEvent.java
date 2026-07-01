package hygge.blog.event;

import hygge.commons.spring.event.BaseHyggeEvent;

import java.time.Clock;

/**
 * @author Xavier
 * @date 2026/5/29
 */
public class FileCacheRefreshEvent extends BaseHyggeEvent<FileCacheRefreshEventInfo> {
    public FileCacheRefreshEvent(FileCacheRefreshEventInfo source) {
        super(source);
    }

    public FileCacheRefreshEvent(FileCacheRefreshEventInfo source, Clock clock) {
        super(source, clock);
    }
}
