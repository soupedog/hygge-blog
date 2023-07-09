package hygge.blog.elasticsearch.service;

import hygge.blog.dao.ArticleDao;
import hygge.blog.dao.CategoryDao;
import hygge.blog.dao.QuoteDao;
import hygge.blog.dao.TopicDao;
import hygge.blog.dao.UserDao;
import hygge.blog.domain.dto.ArticleDto;
import hygge.blog.domain.dto.QuoteDto;
import hygge.blog.domain.mapper.ElasticToDtoMapper;
import hygge.blog.domain.mapper.PoDtoMapper;
import hygge.blog.domain.po.Article;
import hygge.blog.domain.po.Category;
import hygge.blog.domain.po.Quote;
import hygge.blog.domain.po.Topic;
import hygge.blog.domain.po.User;
import hygge.blog.elasticsearch.dao.SearchingCacheDao;
import hygge.blog.elasticsearch.dto.FuzzySearchCache;
import hygge.blog.service.ArticleServiceImpl;
import hygge.web.template.HyggeWebUtilContainer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private TopicDao topicDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private QuoteDao quoteDao;
    @Autowired
    private SearchingCacheDao searchingCacheDao;

    public void freshSingleArticle(String aid, Integer articleId) {
        ArticleDto articleDto = articleService.findArticleDetailByAid(false, aid);
        FuzzySearchCache fuzzySearchCache = ElasticToDtoMapper.INSTANCE.articleDtoToEs(articleDto);
        fuzzySearchCache.setEsId(articleId);
        fuzzySearchCache.setType(FuzzySearchCache.Type.ARTICLE);
        try {
            searchingCacheDao.save(fuzzySearchCache);
        } catch (Exception e) {
            // 7.x 的客户端 能写入，但是无法正常解析 8.x 服务端的返回值(workaround)
        }
    }

    public void freshSingleQuote(Integer quoteId) {
        Quote quote = quoteDao.findById(quoteId).get();
        QuoteDto quoteDto = PoDtoMapper.INSTANCE.poToDto(quote);

        FuzzySearchCache fuzzySearchCache = ElasticToDtoMapper.INSTANCE.quoteDtoToEs(quoteDto);
        fuzzySearchCache.setEsId(quoteId + FuzzySearchCache.INTERVAL);
        fuzzySearchCache.setType(FuzzySearchCache.Type.QUOTE);
        // 时间对句子本身来说其实没有意义，为了落到 ES 时间必填
        fuzzySearchCache.setCreateTs(new Timestamp(System.currentTimeMillis()));
        try {
            searchingCacheDao.save(fuzzySearchCache);
        } catch (Exception e) {
            // 7.x 的客户端 能写入，但是无法正常解析 8.x 服务端的返回值(workaround)
        }
    }

    public void freshArticle() {
        long startTs = System.currentTimeMillis();
        AtomicInteger totalCount = new AtomicInteger(0);

        List<Category> allCategoryList = categoryDao.findAll();
        List<Topic> allTopicList = topicDao.findAll();
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
                Topic currentTopic = allTopicList.stream().filter(topic -> topic.getTopicId().equals(currentCategory.getTopicId())).findFirst().orElse(null);
                User currentUser = allUserList.stream().filter(user -> user.getUserId().equals(article.getUserId())).findFirst().orElse(null);

                ArticleDto articleDto = PoDtoMapper.INSTANCE.poToDto(article);
                articleDto.setUid(currentUser.getUid());
                articleDto.setCid(currentCategory.getCid());

                articleDto.initCategoryTreeInfo(PoDtoMapper.INSTANCE.poToDto(currentTopic), currentCategory, allCategoryList);

                FuzzySearchCache fuzzySearchCache = ElasticToDtoMapper.INSTANCE.articleDtoToEs(articleDto);
                fuzzySearchCache.setEsId(article.getArticleId());
                fuzzySearchCache.setType(FuzzySearchCache.Type.ARTICLE);

                try {
                    searchingCacheDao.save(fuzzySearchCache);
                } catch (Exception e) {
                    // 7.x 的客户端 能写入，但是无法正常解析 8.x 服务端的返回值(workaround)
                }
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

                FuzzySearchCache fuzzySearchCache = ElasticToDtoMapper.INSTANCE.quoteDtoToEs(quoteDto);
                fuzzySearchCache.setEsId(quote.getQuoteId() + FuzzySearchCache.INTERVAL);
                fuzzySearchCache.setType(FuzzySearchCache.Type.QUOTE);
                // 时间对句子本身来说其实没有意义，为了落到 ES 时间必填
                fuzzySearchCache.setCreateTs(new Timestamp(startTs + totalCount.get()));

                try {
                    searchingCacheDao.save(fuzzySearchCache);
                } catch (Exception e) {
                    // 7.x 的客户端 能写入，但是无法正常解析 8.x 服务端的返回值(workaround)
                }
                totalCount.incrementAndGet();
            });
        } while (!quoteTemp.isLast());

        log.info("已刷新句子数 {} 耗时 {} ms", totalCount.get(), System.currentTimeMillis() - startTs);
    }
}
