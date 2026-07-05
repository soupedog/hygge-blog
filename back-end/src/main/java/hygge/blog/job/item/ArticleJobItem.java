package hygge.blog.job.item;

import hygge.blog.domain.local.po.Article;
import hygge.job.BaseHyggeJobItem;

/**
 * @author Xavier
 * @date 2026/7/2
 */
public class ArticleJobItem<PD> extends BaseHyggeJobItem<Article, PD, String> {
    public ArticleJobItem(Article rawData) {
        super(rawData);
    }

    @Override
    public String getUniqueIdentifier() {
        return getRawData().getAid();
    }
}
