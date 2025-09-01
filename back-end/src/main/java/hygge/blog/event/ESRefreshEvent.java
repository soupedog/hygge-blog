package hygge.blog.event;

import hygge.blog.event.base.HyggeEvent;

/**
 * @author Xavier
 * @date 2025/9/1
 */
public class ESRefreshEvent extends HyggeEvent<ESRefreshEventInfo> {
    public ESRefreshEvent(ESRefreshEventInfo source) {
        super(source);
    }

    public ESRefreshEvent(ESRefreshEventInfo source, Long tsOfOccurrence) {
        super(source, tsOfOccurrence);
    }
}
