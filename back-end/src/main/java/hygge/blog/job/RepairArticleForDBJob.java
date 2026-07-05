package hygge.blog.job;

import hygge.blog.domain.local.po.Article;
import hygge.blog.job.item.ArticleJobItem;
import hygge.blog.job.other.BaseBlogExclusiveJob;
import hygge.blog.job.other.HyggeBlogJpaContext;
import hygge.blog.repository.database.ArticleDao;
import hygge.blog.service.local.normal.ArticleServiceImpl;
import hygge.job.HyggeJobBatchItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Xavier
 * @date 2026/7/5
 */
@Service
public class RepairArticleForDBJob extends BaseBlogExclusiveJob<ArticleJobItem<Void>, Article, Void> {
    private final ArticleDao articleDao;
    private final ArticleServiceImpl articleService;

    public RepairArticleForDBJob(ArticleDao articleDao, ArticleServiceImpl articleService) {
        this.articleDao = articleDao;
        this.articleService = articleService;
    }

    @Override
    protected String getJobName() {
        return this.getClass().getSimpleName();
    }

    @Override
    protected List<Article> firstFetchIfNecessary(HyggeBlogJpaContext<Article> context, HyggeJobBatchItem<ArticleJobItem<Void>> jobBatchItem) {
        Pageable pageable = PageRequest.of(0, context.getBatchSize(), Sort.by(Sort.Order.asc("articleId")));
        Page<Article> page = articleDao.findAll(pageable);
        context.setPage(page);

        if (page.isLast()) {
            context.setNoNextPage(true);
        }

        return page.getContent();
    }

    @Override
    protected List<Article> getNextBatch(HyggeBlogJpaContext<Article> context, HyggeJobBatchItem<ArticleJobItem<Void>> jobBatchItem) {
        if (context.isNoNextPage()) {
            return List.of();
        }

        Page<Article> page = context.getPage();
        page = articleDao.findAll(page.nextPageable());
        context.setPage(page);

        if (page.isLast()) {
            context.setNoNextPage(true);
        }

        return page.getContent();
    }

    @Override
    protected ArticleJobItem<Void> createJobItem(HyggeBlogJpaContext<Article> context, HyggeJobBatchItem<ArticleJobItem<Void>> jobBatchItem, Article rawData) {
        return new ArticleJobItem<>(rawData);
    }

    @Override
    protected Void handleSingleItem(HyggeBlogJpaContext<Article> context, ArticleJobItem<Void> jobItem) {
        return null;
    }

    @Override
    protected void batchCompleteHook(HyggeBlogJpaContext<Article> context, HyggeJobBatchItem<ArticleJobItem<Void>> jobBatchItem, List<Article> rawDataList, List<Void> processedDataList) {
        // 更新文章字数
        articleService.updateArticleWordCount(rawDataList);
        super.batchCompleteHook(context, jobBatchItem, rawDataList, processedDataList);
    }
}
