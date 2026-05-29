package hygge.blog.event;

import hygge.blog.event.base.HyggeEvent;

/**
 * @author Xavier
 * @date 2026/5/29
 */
public class FileCacheRefreshEvent extends HyggeEvent<FileCacheRefreshEventInfo> {
    public FileCacheRefreshEvent(FileCacheRefreshEventInfo source) {
        super(source);
    }

    public FileCacheRefreshEvent(FileCacheRefreshEventInfo source, Long tsOfOccurrence) {
        super(source, tsOfOccurrence);
    }
}
