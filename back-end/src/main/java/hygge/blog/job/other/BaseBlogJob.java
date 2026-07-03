package hygge.blog.job.other;

import hygge.job.BaseHyggeJob;
import hygge.job.BaseHyggeJobItem;
import hygge.job.DefaultHyggeJobBatchItem;

/**
 * @author Xavier
 * @date 2026/7/4
 */
public abstract class BaseBlogJob<JI extends BaseHyggeJobItem<RD, PD, ?>, RD, PD>
        extends BaseHyggeJob<HyggeBlogJpaContext<RD>, DefaultHyggeJobBatchItem<JI>, JI, RD, PD> {

    protected BaseBlogJob(int defaultBatchSize, boolean bachAsynchronousEnable) {
        super(defaultBatchSize, bachAsynchronousEnable);
    }

    @Override
    protected HyggeBlogJpaContext<RD> createContext() {
        return new HyggeBlogJpaContext<>();
    }

    @Override
    protected DefaultHyggeJobBatchItem<JI> createJobBatchItem(HyggeBlogJpaContext<RD> context) {
        return new DefaultHyggeJobBatchItem<>();
    }
}
