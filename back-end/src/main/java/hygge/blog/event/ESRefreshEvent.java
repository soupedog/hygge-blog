package hygge.blog.event;


import hygge.commons.spring.event.BaseHyggeEvent;

import java.time.Clock;

/**
 * @author Xavier
 * @date 2025/9/1
 */
public class ESRefreshEvent extends BaseHyggeEvent<ESRefreshEventInfo> {
    public ESRefreshEvent(ESRefreshEventInfo source) {
        super(source);
    }

    public ESRefreshEvent(ESRefreshEventInfo source, Clock clock) {
        super(source, clock);
    }
}
