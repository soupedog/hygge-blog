package hygge.blog.job.other;

import hygge.job.BaseHyggeJob;
import hygge.job.BaseHyggeJobItem;
import hygge.job.HyggeJobBatchItem;

import java.util.List;

/**
 * @author Xavier
 * @date 2026/7/4
 */
public abstract class BaseBlogJob<JI extends BaseHyggeJobItem<RD, PD, ?>, RD, PD>
        extends BaseHyggeJob<HyggeBlogJpaContext<RD>, HyggeJobBatchItem<JI>, JI, RD, PD> {

    @Override
    public HyggeBlogJpaContext<RD> createContext() {
        return new HyggeBlogJpaContext<>();
    }

    @Override
    protected HyggeJobBatchItem<JI> createJobBatchItem(HyggeBlogJpaContext<RD> context) {
        return new HyggeJobBatchItem<>();
    }

    @Override
    protected List<RD> firstFetch(HyggeBlogJpaContext<RD> context, HyggeJobBatchItem<JI> jobBatchItem) {
        List<RD> result = context.getFullData();

        if (result == null || result.isEmpty()) {
            return firstFetchIfNecessary(context, jobBatchItem);
        }
        // 该任务不需要扫描数据库
        context.setNoNextPage(true);
        return result;
    }

    protected abstract List<RD> firstFetchIfNecessary(HyggeBlogJpaContext<RD> context, HyggeJobBatchItem<JI> jobBatchItem);

}
