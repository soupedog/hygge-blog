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
import hygge.blog.domain.local.po.User;
import hygge.blog.repository.database.ArticleDao;
import hygge.blog.repository.database.CategoryDao;
import hygge.blog.repository.database.QuoteDao;
import hygge.blog.repository.database.UserDao;
import hygge.blog.repository.elasticsearch.SearchingCacheDao;
import hygge.blog.service.local.CacheServiceImpl;
import hygge.blog.service.local.normal.ArticleServiceImpl;
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

import java.sql.Timestamp;
import java.util.List;
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
    private ArticleServiceImpl articleService;
    @Autowired
    private CategoryDao categoryDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private QuoteDao quoteDao;
    @Autowired
    private SearchingCacheDao searchingCacheDao;
    @Autowired
    private CacheServiceImpl cacheService;
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

    public void freshSingleArticle(String aid, Integer articleId) {
        ArticleDto articleDto = articleService.findArticleDetailByAid(false, aid);
        ArticleQuoteSearchCache articleQuoteSearchCache = ElasticToDtoMapper.INSTANCE.articleDtoToEs(articleDto);
        articleQuoteSearchCache.setEsId(articleId);
        articleQuoteSearchCache.setType(ArticleQuoteSearchCache.Type.ARTICLE);
        searchingCacheDao.save(articleQuoteSearchCache);
    }

    public void freshSingleQuote(Integer quoteId) {
        Quote quote = quoteDao.findById(quoteId).orElse(null);
        QuoteDto quoteDto = PoDtoMapper.INSTANCE.poToDto(quote);

        ArticleQuoteSearchCache articleQuoteSearchCache = ElasticToDtoMapper.INSTANCE.quoteDtoToEs(quoteDto);
        articleQuoteSearchCache.setEsId(quoteId + ArticleQuoteSearchCache.INTERVAL);
        articleQuoteSearchCache.setType(ArticleQuoteSearchCache.Type.QUOTE);
        // 时间对句子本身来说其实没有意义，为了落到 ES 时间必填
        articleQuoteSearchCache.setCreateTs(new Timestamp(System.currentTimeMillis()));
        searchingCacheDao.save(articleQuoteSearchCache);
    }

    public void freshArticle() {
        long startTs = System.currentTimeMillis();
        AtomicInteger totalCount = new AtomicInteger(0);

        List<Category> allCategoryList = categoryDao.findAll();
        List<User> allUserList = userDao.findAll();
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.asc("articleId")));

        Page<Article> articleTemp = articleDao.findAll(pageable);

        List<Article> articleList = null;
        do {
            if (articleList == null) {
                articleList = articleTemp.getContent();
            } else {
                articleTemp = articleDao.findAll(articleTemp.nextPageable());
                articleList = articleTemp.getContent();
            }

            articleList.forEach(article -> {
                Category currentCategory = allCategoryList.stream().filter(category -> category.getCategoryId().equals(article.getCategoryId())).findFirst().orElse(null);
                User currentUser = allUserList.stream().filter(user -> user.getUserId().equals(article.getUserId())).findFirst().orElse(null);

                ArticleDto articleDto = PoDtoMapper.INSTANCE.poToDto(article);
                articleDto.setUid(currentUser.getUid());
                articleDto.setCid(currentCategory.getCid());

                CategoryTreeInfo categoryTreeInfo = cacheService.getCategoryTreeFormCurrent(currentCategory.getCategoryId());
                articleDto.setCategoryTreeInfo(categoryTreeInfo);

                ArticleQuoteSearchCache articleQuoteSearchCache = ElasticToDtoMapper.INSTANCE.articleDtoToEs(articleDto);
                articleQuoteSearchCache.setEsId(article.getArticleId());
                articleQuoteSearchCache.setType(ArticleQuoteSearchCache.Type.ARTICLE);

                searchingCacheDao.save(articleQuoteSearchCache);
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
            if (quoteList == null) {
                quoteList = quoteTemp.getContent();
            } else {
                quoteTemp = quoteDao.findAll(quoteTemp.nextPageable());
                quoteList = quoteTemp.getContent();
            }

            quoteList.forEach(quote -> {
                QuoteDto quoteDto = PoDtoMapper.INSTANCE.poToDto(quote);

                ArticleQuoteSearchCache articleQuoteSearchCache = ElasticToDtoMapper.INSTANCE.quoteDtoToEs(quoteDto);
                articleQuoteSearchCache.setEsId(quote.getQuoteId() + ArticleQuoteSearchCache.INTERVAL);
                articleQuoteSearchCache.setType(ArticleQuoteSearchCache.Type.QUOTE);
                // 时间对句子本身来说其实没有意义，只因落到 ES 要求时间必填
                articleQuoteSearchCache.setCreateTs(new Timestamp(startTs + totalCount.get()));

                searchingCacheDao.save(articleQuoteSearchCache);
                totalCount.incrementAndGet();
            });
        } while (!quoteTemp.isLast());

        log.info("已刷新句子数 {} 耗时 {} ms", totalCount.get(), System.currentTimeMillis() - startTs);
    }
}
