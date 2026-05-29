package hygge.blog.service.elasticsearch;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
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
import hygge.util.template.HyggeJsonUtilContainer;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static co.elastic.clients.elasticsearch._types.query_dsl.Query.of;

/**
 * @author Xavier
 * @date 2026/5/25
 */
@Repository
public class KeywordSearchServiceImpl extends HyggeJsonUtilContainer {
    private final ElasticsearchOperations operations;
    private final CategoryServiceImpl categoryService;

    public KeywordSearchServiceImpl(ElasticsearchOperations operations, CategoryServiceImpl categoryService) {
        this.operations = operations;
        this.categoryService = categoryService;
    }

    public ArticleSummaryInfo articleKeyWordSearch(String keywords, Pageable pageable) {
        HyggeRequestContext context = HyggeRequestTracker.getContext();
        User currentUser = context.getCurrentLoginUser();
        String secretKey = context.getObject(HyggeRequestContext.Key.SECRET_KEY);
        String uid = currentUser == null ? null : currentUser.getUid();

        List<Category> categoryList = categoryService.getAccessibleCategoryList(secretKey, currentUser, null);

        List<Integer> accessibleCategoryIdList = collectionHelper.filterNonemptyItemAsArrayList(false, categoryList, Category::getCategoryId);

        SearchPage<ArticleQuoteSearchCache> cacheTemp = articleKeyWordSearch(keywords, accessibleCategoryIdList, uid, pageable);

        List<ArticleQuoteSearchCache> resultTemp = cacheTemp
                .getContent()
                .stream()
                .map(SearchHit::getContent)
                .toList();

        long totalCount = cacheTemp.getTotalElements();

        List<ArticleDto> articleSummaryList = collectionHelper.filterNonemptyItemAsArrayList(false, resultTemp, cache -> {
            ArticleDto dto = ElasticToDtoMapper.INSTANCE.esToArticleDto(cache);
            // 缓存里是 DTO 转存的，没有 userId，故此处使用 uid
            // 鉴别并赋值当前用户是否有编辑权限
            Optional.ofNullable(cache.getUid()).ifPresent(uidOfCache -> {
                if (uidOfCache.equals(uid)) {
                    dto.setEditable(true);
                }
            });
            // 模糊查询配合前端不显示顶置
            dto.setOrderGlobal(null);
            dto.setOrderCategory(null);
            return dto;
        });

        return ArticleSummaryInfo.builder()
                .articleSummaryList(articleSummaryList)
                .totalCount(totalCount)
                .build();
    }

    public SearchPage<ArticleQuoteSearchCache> articleKeyWordSearch(String keywords, List<Integer> categoryIdList, String currentUid, Pageable pageable) {
        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(q -> q.bool(b -> b
                        // 1. 关键字模糊匹配（dis_max 取最高分）
                        .must(m -> m.disMax(d -> d
                                .tieBreaker(0.0)
                                .queries(
                                        of(q1 -> q1.match(match -> match.field("title").query(keywords).fuzziness("AUTO"))),
                                        of(q2 -> q2.match(match -> match.field("summary").query(keywords).fuzziness("AUTO"))),
                                        of(q3 -> q3.match(match -> match.field("content").query(keywords).fuzziness("AUTO")))
                                )
                        ))
                        // 2. 限定文章类型
                        .must(m -> m.term(t -> t.field("type").value(ArticleQuoteSearchCache.Type.ARTICLE.name())))
                        // 3. 限定分类ID列表
                        .must(m -> {
                            if (categoryIdList != null && !categoryIdList.isEmpty()) {
                                return m.terms(t -> t
                                        .field("categoryId")
                                        .terms(ts -> ts.value(categoryIdList.stream().map(FieldValue::of).toList()))
                                );
                            }
                            return m.matchAll(ma -> ma);
                        })
                        // 4. 权限控制：(state=ACTIVE) OR (uid=currentUid)
                        .must(m -> m.bool(b2 -> {
                            BoolQuery.Builder boolBuilder = b2
                                    .should(s -> s.term(t -> t.field("state").value(ArticleQuoteSearchCache.StateEnum.ACTIVE.name())))
                                    .minimumShouldMatch("1");

                            // 只在 currentUid 不为 null 时添加 uid 条件
                            if (currentUid != null) {
                                boolBuilder.should(s -> s.term(t -> t.field("uid").value(currentUid)));
                            }

                            return boolBuilder;
                        }))
                ))
                .withPageable(pageable)
                .withSort(s -> s.score(sc -> sc.order(SortOrder.Desc)))
                .withSourceFilter(ARTICLE_SOURCE_FILTER)  // 复用类级别常量
                .build();

        SearchHits<ArticleQuoteSearchCache> searchHits = operations.search(nativeQuery, ArticleQuoteSearchCache.class);
        return SearchHitSupport.searchPageFor(searchHits, pageable);
    }

    public QuoteInfo quoteKeyWordSearch(String keywords, Pageable pageable) {
        HyggeRequestContext context = HyggeRequestTracker.getContext();
        User currentUser = context.getCurrentLoginUser();
        String uid = currentUser == null ? null : currentUser.getUid();

        SearchPage<ArticleQuoteSearchCache> cacheTemp = quoteKeyWordSearch(keywords, uid, pageable);

        List<ArticleQuoteSearchCache> resultTemp = cacheTemp
                .getContent()
                .stream()
                .map(SearchHit::getContent)
                .toList();

        long totalCount = cacheTemp.getTotalElements();

        List<QuoteDto> quoteList = collectionHelper.filterNonemptyItemAsArrayList(false, resultTemp, cache -> {
            QuoteDto dto = ElasticToDtoMapper.INSTANCE.esToQuoteDto(cache);

            // 缓存里是 DTO 转存的，没有 userId，故此处使用 uid
            // 鉴别并赋值当前用户是否有编辑权限
            Optional.ofNullable(dto.getUid()).ifPresent(uidOfCache -> {
                if (uidOfCache.equals(uid)) {
                    dto.setEditable(true);
                }
            });
            return dto;
        });


        return QuoteInfo.builder()
                .quoteList(quoteList)
                .totalCount(totalCount)
                .build();
    }


    public SearchPage<ArticleQuoteSearchCache> quoteKeyWordSearch(String keywords, String currentUid, Pageable pageable) {
        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(q -> q.bool(b -> b
                        // 1. 关键字模糊匹配（dis_max 取最高分）
                        .must(m -> m.disMax(d -> d
                                .tieBreaker(0.0)
                                .queries(
                                        of(q1 -> q1.match(match -> match.field("source").query(keywords).fuzziness("AUTO"))),
                                        of(q2 -> q2.match(match -> match.field("remarks").query(keywords).fuzziness("AUTO"))),
                                        of(q3 -> q3.match(match -> match.field("content").query(keywords).fuzziness("AUTO")))
                                )
                        ))
                        // 2. 限定文章类型
                        .must(m -> m.term(t -> t.field("type").value(ArticleQuoteSearchCache.Type.QUOTE.name())))
                        // 3. 权限控制：(state=ACTIVE) OR (uid=currentUid)
                        .must(m -> m.bool(b2 -> {
                            BoolQuery.Builder boolBuilder = b2
                                    .should(s -> s.term(t -> t.field("state").value(ArticleQuoteSearchCache.StateEnum.ACTIVE.name())))
                                    .minimumShouldMatch("1");

                            // 只在 currentUid 不为 null 时添加 uid 条件
                            if (currentUid != null) {
                                boolBuilder.should(s -> s.term(t -> t.field("uid").value(currentUid)));
                            }

                            return boolBuilder;
                        }))
                ))
                .withPageable(pageable)
                .withSort(s -> s.score(sc -> sc.order(SortOrder.Desc)))
                .withSourceFilter(QUOTE_SOURCE_FILTER)
                .build();

        SearchHits<ArticleQuoteSearchCache> searchHits = operations.search(nativeQuery, ArticleQuoteSearchCache.class);
        return SearchHitSupport.searchPageFor(searchHits, pageable);
    }

    // 类级别常量，排除 content 字段，所有方法共享
    private static final FetchSourceFilter ARTICLE_SOURCE_FILTER = new FetchSourceFilter(
            new String[]{
                    "esId", "type", "aid", "configuration", "categoryTreeInfo",
                    "categoryId", "cid", "uid", "title", "imageSrc",
                    "summary", "wordCount", "pageViews", "selfPageViews",
                    "state", "createTs", "lastUpdateTs"
            },
            new String[]{"content"}
    );

    private static final FetchSourceFilter QUOTE_SOURCE_FILTER = new FetchSourceFilter(
            new String[]{
                    "esId", "type", "quoteId", "source", "content", "portal", "remarks", "uid", "imageSrc",
                    "state", "createTs", "lastUpdateTs"
            },
            new String[0]
    );
}