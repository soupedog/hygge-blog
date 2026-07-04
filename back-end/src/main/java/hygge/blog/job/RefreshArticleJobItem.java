package hygge.blog.job;

import hygge.blog.domain.local.po.Article;
import hygge.job.BaseHyggeJobItem;

/**
 * @author Xavier
 * @date 2026/7/2
 */
public class RefreshArticleJobItem<PD> extends BaseHyggeJobItem<Article, PD, String> {
    protected RefreshArticleJobItem(Article rawData) {
        super(rawData);
    }

    @Override
    public String getUniqueIdentifier() {
        return getRawData().getAid();
    }
}
