package hygge.blog.service.local;

import hygge.blog.common.HyggeRequestContext;
import hygge.blog.common.HyggeRequestTracker;
import hygge.blog.domain.local.dto.ArticleDto;
import hygge.blog.domain.local.dto.QuoteDto;
import hygge.blog.domain.local.dto.QuoteInfo;
import hygge.blog.domain.local.dto.inner.ArticleSummaryInfo;
import hygge.blog.domain.local.enums.UserTypeEnum;
import hygge.blog.common.mapper.ElasticToDtoMapper;
import hygge.blog.domain.local.po.Category;
import hygge.blog.domain.local.po.User;
import hygge.blog.domain.local.dto.ArticleQuoteSearchCache;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.StringQuery;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Xavier
 * @date 2022/8/30
 */
@Service
public class KeywordSearchServiceImpl {
    /**
     * ES 文章查询结果排除 "content"
     */
    private static final FetchSourceFilter ARTICLE_SUMMARY_SOURCE_FILTER = new FetchSourceFilter(null, new String[]{"content"});
    /**
     * ES 查询结果按 _score 倒排(大到小)
     */
    private static final Sort SCORE_DESC_SORT = Sort.by(new Sort.Order(Sort.Direction.DESC, "_score"));
    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    private CategoryServiceImpl categoryService;

    public ArticleSummaryInfo doArticleFuzzySearch(String keyword, Integer currentPage, Integer pageSize) {
        HyggeRequestContext context = HyggeRequestTracker.getContext();
        User currentUser = context.getCurrentLoginUser();

        List<Category> allowableCategoryList = categoryService.getAccessibleCategoryList(currentUser, null);

        SearchHits<ArticleQuoteSearchCache> resultTemp = keywordSearch(keyword, ArticleQuoteSearchCache.Type.ARTICLE, allowableCategoryList, currentPage, pageSize);

        List<ArticleDto> articleSummaryList = new ArrayList<>(pageSize);

        for (SearchHit<ArticleQuoteSearchCache> itemTemp : resultTemp) {
            ArticleQuoteSearchCache item = itemTemp.getContent();
            ArticleDto articleDto = ElasticToDtoMapper.INSTANCE.esToArticleDto(item);
            // 模糊查询配合前端不显示顶置
            articleDto.setOrderGlobal(null);
            articleDto.setOrderCategory(null);
            articleSummaryList.add(articleDto);
        }

        return ArticleSummaryInfo.builder()
                .articleSummaryList(articleSummaryList)
                .totalCount(resultTemp.getTotalHits())
                .build();
    }

    public QuoteInfo doKeywordSearch(String keyword, Integer currentPage, Integer pageSize) {
        SearchHits<ArticleQuoteSearchCache> resultTemp = keywordSearch(keyword, ArticleQuoteSearchCache.Type.QUOTE, null, currentPage, pageSize);

        List<QuoteDto> quoteList = new ArrayList<>(pageSize);

        for (SearchHit<ArticleQuoteSearchCache> itemTemp : resultTemp) {
            ArticleQuoteSearchCache item = itemTemp.getContent();
            QuoteDto quoteDto = ElasticToDtoMapper.INSTANCE.esToQuoteDto(item);
            quoteList.add(quoteDto);
        }

        return QuoteInfo.builder()
                .quoteList(quoteList)
                .totalCount(resultTemp.getTotalHits())
                .build();
    }

    public SearchHits<ArticleQuoteSearchCache> keywordSearch(String keyword, ArticleQuoteSearchCache.Type type, Collection<Category> allowableCategoryList, Integer currentPage, Integer pageSize) {
        // 聚合后的最终条件构造器
        BoolQueryBuilder rootQueryBuilder = QueryBuilders.boolQuery();

        // 类型要求(文章或者句子收藏)
        BoolQueryBuilder typeRequirement = QueryBuilders.boolQuery();
        typeRequirement.should(QueryBuilders.termQuery("type", type.name()));
        rootQueryBuilder.must(typeRequirement);

        // 关键字要求
        MultiMatchQueryBuilder keywordRequirement;
        if (type.equals(ArticleQuoteSearchCache.Type.ARTICLE)) {
            // 文章类别展示编号必须与下列之一匹配
            BoolQueryBuilder cidRequirement = QueryBuilders.boolQuery();
            List<String> allowableCidList = allowableCategoryList.stream()
                    .map(Category::getCid)
                    .toList();
            for (String cid : allowableCidList) {
                cidRequirement.should(QueryBuilders.termQuery("cid", cid));
            }
            rootQueryBuilder.must(cidRequirement);

            keywordRequirement = QueryBuilders
                    // 指定多重匹配字段、关键字
                    .multiMatchQuery(keyword, "title", "summary", "content")
                    // 指定模式 _score 按 title 、summary、content 之和(默认为 BEST_FIELDS 只取 title、summary、content 中的最高分)
                    .type(MultiMatchQueryBuilder.Type.MOST_FIELDS);
        } else {
            keywordRequirement = QueryBuilders
                    // 指定多重匹配字段、关键字
                    .multiMatchQuery(keyword, "source", "remarks", "content")
                    // 指定模式 _score 按 source 、remarks、content 之和(默认为 BEST_FIELDS 只取 source 、remarks、content 中的最高分)
                    .type(MultiMatchQueryBuilder.Type.MOST_FIELDS);
        }
        rootQueryBuilder.must(keywordRequirement);

        HyggeRequestContext context = HyggeRequestTracker.getContext();
        User currentUser = context.getCurrentLoginUser();
        if (currentUser == null || !currentUser.getUserType().equals(UserTypeEnum.ROOT)) {
            // 状态要求(非管理员只允许查询 ACTIVE 状态内容)
            BoolQueryBuilder stateRequirement = QueryBuilders.boolQuery();
            stateRequirement.should(QueryBuilders.termQuery("state", ArticleQuoteSearchCache.StateEnum.ACTIVE.name()));
            rootQueryBuilder.must(stateRequirement);
        }

        String source = rootQueryBuilder.toString();
        StringQuery rootQuery = new StringQuery(source);

        if (type.equals(ArticleQuoteSearchCache.Type.ARTICLE)) {
            // 设置查询结果过虑掉 content(用于展示文章摘要信息而已，所以不需要主体内容)
            rootQuery.addSourceFilter(ARTICLE_SUMMARY_SOURCE_FILTER);
        }

        // 设置分页 当前页(从 0 开始为第一页) 页容量
        PageRequest pageRequest = PageRequest.of(currentPage - 1, pageSize, SCORE_DESC_SORT);
        rootQuery.setPageable(pageRequest);
        return elasticsearchRestTemplate.search(rootQuery, ArticleQuoteSearchCache.class);
    }

}
