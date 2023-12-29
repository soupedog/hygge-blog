package hygge.blog.service.elasticsearch;

import hygge.blog.common.mapper.ElasticToDtoMapper;
import hygge.blog.common.mapper.PoDtoMapper;
import hygge.blog.domain.local.dto.ArticleDto;
import hygge.blog.domain.local.dto.ArticleQuoteSearchCache;
import hygge.blog.domain.local.dto.QuoteDto;
import hygge.blog.domain.local.dto.inner.CategoryTreeInfo;
import hygge.blog.domain.local.po.Article;
import hygge.blog.domain.local.po.Category;
import hygge.blog.domain.local.po.Quote;
import hygge.blog.repository.database.ArticleDao;
import hygge.blog.repository.database.CategoryDao;
import hygge.blog.repository.database.QuoteDao;
import hygge.blog.repository.elasticsearch.SearchingCacheDao;
import hygge.blog.service.local.CacheServiceImpl;
import hygge.blog.service.local.normal.QuoteServiceImpl;
import hygge.web.template.HyggeWebUtilContainer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Xavier
 * @date 2022/8/29
 */
@Slf4j
@Service
public class RefreshElasticSearchServiceImpl extends HyggeWebUtilContainer {
    @Autowired
    private ArticleDao articleDao;
    @Autowired
    private CategoryDao categoryDao;
    @Autowired
    private QuoteDao quoteDao;
    @Autowired
    private QuoteServiceImpl quoteService;
    @Autowired
    private CacheServiceImpl cacheService;
    @Autowired
    private SearchingCacheDao searchingCacheDao;
    @Autowired
    private ElasticsearchOperations operations;

    public void checkAndInitIndex() {
        // Spring 提供的注解生成索引方案
        IndexOperations indexOperations = operations.indexOps(ArticleQuoteSearchCache.class);

        if (!indexOperations.exists()) {
            log.info("{} 索引不存在，开始创建", ArticleQuoteSearchCache.class.getSimpleName());
            // 创建索引
            indexOperations.create();
            // 配置映射
            indexOperations.putMapping(indexOperations.createMapping());
        }
    }

    public void freshSingleArticleAsync(Integer articleId) {
        CompletableFuture.runAsync(() -> {
            freshSingleArticle(articleId);
        }).exceptionally(e -> {
            log.error("刷新文章(" + articleId + ") 模糊搜索数据 失败.", e);
            return null;
        });
    }

    public void freshSingleArticle(Integer articleId) {
        Article article = articleDao.findById(articleId).orElse(null);
        Category currentCategory = categoryDao.findById(article.getCategoryId()).orElse(null);
        CategoryTreeInfo categoryTreeInfo = cacheService.getCategoryTreeFormCurrent(currentCategory.getCategoryId());

        freshSingleArticle(article, currentCategory, categoryTreeInfo);
    }

    public void freshSingleArticle(Article article, Category currentCategory, CategoryTreeInfo categoryTreeInfo) {
        ArticleDto articleDto = PoDtoMapper.INSTANCE.poToDto(article);
        articleDto.setUid(cacheService.userIdToUid(article.getUserId()));
        articleDto.setCid(currentCategory.getCid());

        articleDto.setCategoryTreeInfo(categoryTreeInfo);

        ArticleQuoteSearchCache articleQuoteSearchCache = ElasticToDtoMapper.INSTANCE.articleDtoToEs(articleDto);
        articleQuoteSearchCache.initEsId(article.getArticleId(), ArticleQuoteSearchCache.Type.ARTICLE);
        articleQuoteSearchCache.setCategoryId(article.getCategoryId());
        articleQuoteSearchCache.setType(ArticleQuoteSearchCache.Type.ARTICLE);
        searchingCacheDao.save(articleQuoteSearchCache);
    }

    public void freshSingleQuote(Integer quoteId) {
        Quote quote = quoteService.findQuoteByQuoteId(quoteId, false);
        QuoteDto quoteDto = PoDtoMapper.INSTANCE.poToDto(quote);
        // userId → uid
        String authorUid = cacheService.userIdToUid(quote.getUserId());
        quoteDto.setUid(authorUid);

        ArticleQuoteSearchCache articleQuoteSearchCache = ElasticToDtoMapper.INSTANCE.quoteDtoToEs(quoteDto);
        articleQuoteSearchCache.initEsId(quoteId, ArticleQuoteSearchCache.Type.QUOTE);
        articleQuoteSearchCache.setType(ArticleQuoteSearchCache.Type.QUOTE);
        searchingCacheDao.save(articleQuoteSearchCache);
    }

    public void freshAllArticle() {
        long startTs = System.currentTimeMillis();
        AtomicInteger totalCount = new AtomicInteger(0);

        List<Category> allCategoryList = categoryDao.findAll();
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.asc("articleId")));

        Page<Article> articleTemp = articleDao.findAll(pageable);

        List<Article> articleList = null;
        do {
            if (articleList != null) {
                articleTemp = articleDao.findAll(articleTemp.nextPageable());
            }
            articleList = articleTemp.getContent();

            articleList.forEach(article -> {
                Category currentCategory = allCategoryList.stream().filter(category -> category.getCategoryId().equals(article.getCategoryId())).findFirst().orElse(null);
                CategoryTreeInfo categoryTreeInfo = cacheService.getCategoryTreeFormCurrent(currentCategory.getCategoryId());

                freshSingleArticle(article, currentCategory, categoryTreeInfo);

                totalCount.incrementAndGet();
            });
        } while (!articleTemp.isLast());

        log.info("已刷新文章数 {} 耗时 {} ms", totalCount.get(), System.currentTimeMillis() - startTs);
    }

    public void freshQuote() {
        long startTs = System.currentTimeMillis();
        AtomicInteger totalCount = new AtomicInteger(0);

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.asc("quoteId")));

        Page<Quote> quoteTemp = quoteDao.findAll(pageable);

        List<Quote> quoteList = null;
        do {
            if (quoteList != null) {
                quoteTemp = quoteDao.findAll(quoteTemp.nextPageable());
            }
            quoteList = quoteTemp.getContent();

            quoteList.forEach(quote -> {
                QuoteDto quoteDto = PoDtoMapper.INSTANCE.poToDto(quote);

                ArticleQuoteSearchCache articleQuoteSearchCache = ElasticToDtoMapper.INSTANCE.quoteDtoToEs(quoteDto);
                articleQuoteSearchCache.initEsId(quote.getQuoteId(), ArticleQuoteSearchCache.Type.QUOTE);
                articleQuoteSearchCache.setType(ArticleQuoteSearchCache.Type.QUOTE);

                searchingCacheDao.save(articleQuoteSearchCache);
                totalCount.incrementAndGet();
            });
        } while (!quoteTemp.isLast());

        log.info("已刷新句子数 {} 耗时 {} ms", totalCount.get(), System.currentTimeMillis() - startTs);
    }
}
