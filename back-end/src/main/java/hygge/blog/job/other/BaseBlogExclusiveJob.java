package hygge.blog.job.other;

import hygge.job.BaseHyggeJobItem;
import hygge.job.HyggeJobBatchItem;
import hygge.job.SimpleHyggeExclusiveJob;

/**
 * @author Xavier
 * @date 2026/7/4
 */
public abstract class BaseBlogExclusiveJob<JI extends BaseHyggeJobItem<RD, PD, ?>, RD, PD>
        extends SimpleHyggeExclusiveJob<HyggeBlogJpaContext<RD>, JI, RD, PD> {

    @Override
    public HyggeBlogJpaContext<RD> createContext() {
        return new HyggeBlogJpaContext<>();
    }

    @Override
    protected HyggeJobBatchItem<JI> createJobBatchItem(HyggeBlogJpaContext<RD> context) {
        return new HyggeJobBatchItem<>();
    }
}
