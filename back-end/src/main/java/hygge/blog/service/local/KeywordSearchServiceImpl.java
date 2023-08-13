package hygge.blog.service.local;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ScoreSort;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.TermsSetQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.SourceConfig;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import hygge.blog.common.HyggeRequestContext;
import hygge.blog.common.HyggeRequestTracker;
import hygge.blog.common.mapper.ElasticToDtoMapper;
import hygge.blog.domain.local.dto.ArticleDto;
import hygge.blog.domain.local.dto.ArticleQuoteSearchCache;
import hygge.blog.domain.local.dto.QuoteDto;
import hygge.blog.domain.local.dto.QuoteInfo;
import hygge.blog.domain.local.dto.inner.ArticleSummaryInfo;
import hygge.blog.domain.local.po.Category;
import hygge.blog.domain.local.po.User;
import hygge.blog.service.local.normal.CategoryServiceImpl;
import hygge.commons.exception.ExternalRuntimeException;
import hygge.web.template.HyggeWebUtilContainer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Xavier
 * @date 2022/8/30
 */
@Slf4j
@Service
public class KeywordSearchServiceImpl extends HyggeWebUtilContainer {
    @Autowired
    private CategoryServiceImpl categoryService;
    @Autowired
    private ElasticsearchClient elasticsearchClient;

    public ArticleSummaryInfo doArticleFuzzySearch(String keyword, Integer currentPage, Integer pageSize) {
        HyggeRequestContext context = HyggeRequestTracker.getContext();
        User currentUser = context.getCurrentLoginUser();

        List<Category> allowableCategoryList = categoryService.getAccessibleCategoryList(currentUser, null);

        SearchResponse<ArticleQuoteSearchCache> resultTemp = keywordSearch(keyword, ArticleQuoteSearchCache.Type.ARTICLE, allowableCategoryList, currentPage, pageSize);

        List<ArticleDto> articleSummaryList = new ArrayList<>(pageSize);

        resultTemp.hits().hits().forEach(itemTemp -> {
            ArticleQuoteSearchCache item = itemTemp.source();
            ArticleDto articleDto = ElasticToDtoMapper.INSTANCE.esToArticleDto(item);
            // 模糊查询配合前端不显示顶置
            articleDto.setOrderGlobal(null);
            articleDto.setOrderCategory(null);
            articleSummaryList.add(articleDto);
        });

        return ArticleSummaryInfo.builder()
                .articleSummaryList(articleSummaryList)
                .totalCount(Optional.ofNullable(resultTemp.hits().total()).map(TotalHits::value).orElse(0L))
                .build();
    }

    public QuoteInfo doKeywordSearch(String keyword, Integer currentPage, Integer pageSize) {
        SearchResponse<ArticleQuoteSearchCache> resultTemp = keywordSearch(keyword, ArticleQuoteSearchCache.Type.QUOTE, null, currentPage, pageSize);

        List<QuoteDto> quoteList = new ArrayList<>(pageSize);

        resultTemp.hits().hits().forEach(itemTemp -> {
            ArticleQuoteSearchCache item = itemTemp.source();
            QuoteDto quoteDto = ElasticToDtoMapper.INSTANCE.esToQuoteDto(item);
            quoteList.add(quoteDto);
        });

        return QuoteInfo.builder()
                .quoteList(quoteList)
                .totalCount(Optional.ofNullable(resultTemp.hits().total()).map(TotalHits::value).orElse(0L))
                .build();
    }

    public SearchResponse<ArticleQuoteSearchCache> keywordSearch(String keyword, ArticleQuoteSearchCache.Type type, Collection<Category> allowableCategoryList, Integer currentPage, Integer pageSize) {
        HyggeRequestContext context = HyggeRequestTracker.getContext();
        boolean isArticle = ArticleQuoteSearchCache.Type.ARTICLE.equals(type);
        boolean notMaintainer = !context.isMaintainer();

        // 限定查询目标类别
        Query typeRequirement = BoolQuery.of(bool -> bool.should(TermQuery.of(term -> term.field("type").value(type.name()))._toQuery()))._toQuery();

        // 限定查询目标状态
        Query statusRequirement;
        if (notMaintainer) {
            // 非管理员不允许搜索非激活状态的目标
            statusRequirement = BoolQuery.of(bool -> bool.should(TermQuery.of(term -> term.field("state").value(ArticleQuoteSearchCache.StateEnum.ACTIVE.name()))._toQuery()))._toQuery();
        } else {
            statusRequirement = null;
        }

        // 限定文章查询类别
        Query categoryRequirement;
        if (isArticle && notMaintainer) {
            // 非管理员仅允许查询有权限的类别文章
            ArrayList<String> allowableCategoryIdStringValList = collectionHelper.filterNonemptyItemAsArrayList(false, allowableCategoryList, (item -> parameterHelper.string(item.getCategoryId())));
            categoryRequirement = BoolQuery.of(bool -> bool.should(TermsSetQuery.of(termsSet -> termsSet.field("categoryId").terms(allowableCategoryIdStringValList))._toQuery()))._toQuery();
        } else {
            categoryRequirement = null;
        }

        // 限定搜索关键字
        Query keywordRequirement = new MultiMatchQuery.Builder()
                .fields("title", "summary", "content")
                // BestFields 指多重字段匹配得分最高的作为当前记录的最终得分，MostFields 代表多字段匹配得分求和作为最终得分，查询结果中，得分高的会排在前面
                // 其他类型见 https://www.elastic.co/guide/cn/elasticsearch/guide/current/_cross_fields_queries.html
                .type(TextQueryType.MostFields)
                // 查询容错步长，可选项有四个 0、1、2、auto(不开查询容错，查询效率肯定是最高的)
                // 1 代表输入 cat ，可能会查到 car ，默认为 0 ，即不允许容错，需要关键字完全匹配
                // auto 则是根据输入内容长度自适应容错步长，输入的内容长容错也就长
                .fuzziness("2")
                .query(keyword).build()
                ._toQuery();

        // 限定返回值内容
        SourceConfig sourceConfig;
        if (isArticle) {
            // 文章查询结果返回值不需要 content 字段节省带宽
            sourceConfig = SourceConfig.of(builder -> builder.filter(filter -> filter.excludes("content")));
        } else {
            sourceConfig = null;
        }

        // 分数倒排顺序
        ScoreSort scoreSort = ScoreSort.of(builder -> builder.order(SortOrder.Desc));
        SortOptions sortOptions = SortOptions.of(builder -> builder.score(scoreSort));

        SearchRequest searchRequest = SearchRequest.of(builder ->
                builder.index("fuzzy_search_cache")
                        .query(q -> q
                                .bool(q2 -> {
                                            // 可空要求全不为空
                                            if (statusRequirement != null && categoryRequirement != null) {
                                                return q2.must(typeRequirement)
                                                        .must(statusRequirement)
                                                        .must(categoryRequirement)
                                                        .must(keywordRequirement);
                                                // 可空要求全为空
                                            } else if (statusRequirement == null && categoryRequirement == null) {
                                                return q2.must(typeRequirement)
                                                        .must(keywordRequirement);
                                            } else {
                                                // 可空要求至少有一个不为空
                                                return q2.must(typeRequirement)
                                                        .must(Objects.requireNonNullElse(statusRequirement, categoryRequirement))
                                                        .must(keywordRequirement);
                                            }
                                        }
                                ))
                        // 从第几个开始
                        .from((currentPage - 1) * pageSize)
                        // 从开始位置往后读取多少个结果(包括开始位置)
                        .size(pageSize)
                        .sort(sortOptions)
                        .source(sourceConfig)
        );

        try {
            return elasticsearchClient.search(searchRequest, ArticleQuoteSearchCache.class);
        } catch (Exception e) {
            throw new ExternalRuntimeException("Fail to execute key word search.", e);
        }
    }
}
