package hygge.blog.job;

import hygge.blog.domain.local.dto.ArticleQuoteSearchCache;
import hygge.blog.domain.local.dto.inner.CategoryTreeInfo;
import hygge.blog.domain.local.po.Article;
import hygge.blog.domain.local.po.Category;
import hygge.blog.job.item.RefreshArticleJobItem;
import hygge.blog.job.key.RefreshArticleJobKey;
import hygge.blog.job.other.BaseBlogJob;
import hygge.blog.job.other.HyggeBlogJpaContext;
import hygge.blog.repository.database.ArticleDao;
import hygge.blog.repository.database.CategoryDao;
import hygge.blog.service.elasticsearch.ElasticSearchServiceImpl;
import hygge.blog.service.local.CacheServiceImpl;
import hygge.job.HyggeJobBatchItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Xavier
 * @date 2026/7/2
 */
@Service
public class RefreshArticleCacheJob extends BaseBlogJob<RefreshArticleJobItem<ArticleQuoteSearchCache>, Article, ArticleQuoteSearchCache> {
    private final ArticleDao articleDao;
    private final CategoryDao categoryDao;
    private final ElasticSearchServiceImpl refreshElasticSearchService;
    private final CacheServiceImpl cacheService;

    public RefreshArticleCacheJob(ArticleDao articleDao, CategoryDao categoryDao, ElasticSearchServiceImpl refreshElasticSearchService, CacheServiceImpl cacheService) {
        this.articleDao = articleDao;
        this.categoryDao = categoryDao;
        this.refreshElasticSearchService = refreshElasticSearchService;
        this.cacheService = cacheService;
    }

    @Override
    protected String getJobName() {
        return this.getClass().getSimpleName();
    }

    @Override
    protected List<Article> firstFetch(HyggeBlogJpaContext<Article> context, HyggeJobBatchItem<RefreshArticleJobItem<ArticleQuoteSearchCache>> jobBatchItem) {
        Pageable pageable = PageRequest.of(0, context.getBatchSize(), Sort.by(Sort.Order.asc("articleId")));
        Page<Article> page = articleDao.findAll(pageable);
        context.setPage(page);

        if (page.isLast()) {
            context.setNoNextPage(true);
        }

        List<Article> result = page.getContent();

        if (!result.isEmpty()) {
            // 全量文章类别信息
            List<Category> allCategoryList = categoryDao.findAll();
            Map<Integer, Category> allCategoryMap = allCategoryList.stream()
                    .collect(
                            Collectors.toMap(
                                    Category::getCategoryId,
                                    Function.identity()
                            )
                    );

            context.saveObject(RefreshArticleJobKey.ALL_CATEGORY_MAP, allCategoryMap);
        }

        return result;
    }

    @Override
    protected List<Article> getNextBatch(HyggeBlogJpaContext<Article> context, HyggeJobBatchItem<RefreshArticleJobItem<ArticleQuoteSearchCache>> jobBatchItem) {
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
    protected RefreshArticleJobItem<ArticleQuoteSearchCache> createJobItem(HyggeBlogJpaContext<Article> context, HyggeJobBatchItem<RefreshArticleJobItem<ArticleQuoteSearchCache>> jobBatchItem, Article rawData) {
        return new RefreshArticleJobItem<>(rawData);
    }

    @Override
    protected ArticleQuoteSearchCache handleSingleItem(HyggeBlogJpaContext<Article> context, RefreshArticleJobItem<ArticleQuoteSearchCache> jobItem) {
        Article article = jobItem.getRawData();

        Map<Integer, Category> allCategoryMap = context.getObject(RefreshArticleJobKey.ALL_CATEGORY_MAP);
        Category currentCategory = allCategoryMap.get(article.getCategoryId());
        // 数据无误时，文章必属于全部文章类别中的一种，不可能空指针异常
        CategoryTreeInfo categoryTreeInfo = cacheService.getCategoryTreeFormCurrent(currentCategory.getCategoryId());

        return refreshElasticSearchService.buildEsDto(article, currentCategory, categoryTreeInfo);
    }

    @Override
    protected void batchCompleteHook(HyggeBlogJpaContext<Article> context, HyggeJobBatchItem<RefreshArticleJobItem<ArticleQuoteSearchCache>> jobBatchItem, List<Article> rawDataCollection, List<ArticleQuoteSearchCache> processedDataCollection) {
        refreshElasticSearchService.save(processedDataCollection);
        super.batchCompleteHook(context, jobBatchItem, rawDataCollection, processedDataCollection);
    }
}
