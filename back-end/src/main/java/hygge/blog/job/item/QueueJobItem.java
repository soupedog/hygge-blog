package hygge.blog.job.item;

import hygge.blog.domain.local.po.Quote;
import hygge.job.BaseHyggeJobItem;

/**
 * @author Xavier
 * @date 2026/7/5
 */
public class QueueJobItem<PD> extends BaseHyggeJobItem<Quote, PD, Integer> {
    public QueueJobItem(Quote rawData) {
        super(rawData);
    }

    @Override
    public Integer getUniqueIdentifier() {
        return getRawData().getQuoteId();
    }
}
