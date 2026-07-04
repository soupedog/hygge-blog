package hygge.blog.job.other;

import hygge.job.BaseHyggeJob;
import hygge.job.BaseHyggeJobItem;
import hygge.job.HyggeJobBatchItem;

/**
 * @author Xavier
 * @date 2026/7/4
 */
public abstract class BaseBlogJob<JI extends BaseHyggeJobItem<RD, PD, ?>, RD, PD>
        extends BaseHyggeJob<HyggeBlogJpaContext<RD>, HyggeJobBatchItem<JI>, JI, RD, PD> {

    @Override
    protected HyggeBlogJpaContext<RD> createContext() {
        return new HyggeBlogJpaContext<>();
    }

    @Override
    protected HyggeJobBatchItem<JI> createJobBatchItem(HyggeBlogJpaContext<RD> context) {
        return new HyggeJobBatchItem<>();
    }
}
