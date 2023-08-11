package hygge.blog.service.local;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.search.SourceConfig;
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
import hygge.blog.service.local.normal.CategoryServiceImpl;
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

    public SearchHits<ArticleQuoteSearchCache> keywordSearch2(String keyword, ArticleQuoteSearchCache.Type type, Collection<Category> allowableCategoryList, Integer currentPage, Integer pageSize) {
        // 查询结果返回值不需要 content 字段节省带宽
        SourceConfig sourceConfig = SourceConfig.of(builder -> builder.filter(filter -> filter.excludes("content")));

        // 多重字段进行全文搜索
        Query keywordRequirement = new MultiMatchQuery.Builder()
                .fields("title", "summary", "content")
                // BestFields 指多重字段匹配得分最高的作为当前记录的最终得分，MostFields 代表多字段匹配得分求和作为最终得分，查询结果中，得分高的会排在前面
                // 其他类型见 https://www.elastic.co/guide/cn/elasticsearch/guide/current/_cross_fields_queries.html
                .type(TextQueryType.MostFields)
                // 查询容错步长，可选项有四个 0、1、2、auto(不开查询容错，查询效率肯定是最高的)
                // 1 代表输入 cat ，可能会查到 car ，默认为 0 ，即不允许容错，需要关键字完全匹配
                // auto 则是根据输入内容长度自适应容错步长，输入的内容长容错也就长
                .fuzziness("0")
                .query(keyword).build()
                ._toQuery();

        Query statusRequirement = BoolQuery.of(b -> b.should(TermQuery.of(t -> t.field("state").value("DRAFT"))._toQuery()))._toQuery();

        SearchRequest searchRequest = SearchRequest.of(builder ->
                builder.index("fuzzy_search_cache")
                        .query(q -> q
                                .bool(q2 -> q2
                                        .must(keywordRequirement)
                                        .must(statusRequirement)
                                ))
                        // 从第几个开始
                        .from((currentPage - 1) * pageSize)
                        // 从开始位置往后读取多少个结果(包括开始位置)
                        .size(pageSize)
                        .source(sourceConfig)
        );

        log.info("查询语句：{}", searchRequest);

        try {
            SearchResponse<FuzzySearchCache> responseTemp = elasticsearchClient.search(searchRequest, FuzzySearchCache.class);
            log.info("共计： {}", Optional.ofNullable(responseTemp.hits().total()).map(TotalHits::value).orElse(0L));

            List<FuzzySearchCache> result = new ArrayList<>();

            responseTemp.hits().hits().forEach(item -> result.add(item.source()));

            return success(result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


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
