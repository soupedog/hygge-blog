package hygge.blog.job.item;

import hygge.blog.domain.local.po.Quote;
import hygge.job.BaseHyggeJobItem;

/**
 * @author Xavier
 * @date 2026/7/5
 */
public class RefreshQueueJobItem<PD> extends BaseHyggeJobItem<Quote, PD, Integer> {
    public RefreshQueueJobItem(Quote rawData) {
        super(rawData);
    }

    @Override
    public Integer getUniqueIdentifier() {
        return getRawData().getQuoteId();
    }
}
