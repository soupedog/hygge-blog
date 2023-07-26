package hygge.blog.service.local.normal;

import hygge.blog.common.HyggeRequestContext;
import hygge.blog.common.HyggeRequestTracker;
import hygge.blog.common.mapper.MapToAnyMapper;
import hygge.blog.common.mapper.OverrideMapper;
import hygge.blog.common.mapper.PoDtoMapper;
import hygge.blog.domain.local.bo.BlogSystemCode;
import hygge.blog.domain.local.dto.ArticleDto;
import hygge.blog.domain.local.dto.inner.ArticleSummaryInfo;
import hygge.blog.domain.local.dto.inner.CategoryTreeInfo;
import hygge.blog.domain.local.enums.BackgroundMusicTypeEnum;
import hygge.blog.domain.local.enums.MediaPlayTypeEnum;
import hygge.blog.domain.local.enums.UserTypeEnum;
import hygge.blog.domain.local.po.Article;
import hygge.blog.domain.local.po.ArticleCountInfo;
import hygge.blog.domain.local.po.Category;
import hygge.blog.domain.local.po.User;
import hygge.blog.domain.local.po.inner.ArticleConfiguration;
import hygge.blog.repository.database.ArticleDao;
import hygge.blog.service.local.CacheServiceImpl;
import hygge.blog.service.local.RefreshElasticSearchServiceImpl;
import hygge.commons.exception.LightRuntimeException;
import hygge.util.UtilCreator;
import hygge.util.bo.ColumnInfo;
import hygge.util.definition.DaoHelper;
import hygge.web.template.HyggeWebUtilContainer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * @author Xavier
 * @date 2022/7/24
 */
@Slf4j
@Service
public class ArticleServiceImpl extends HyggeWebUtilContainer {
    private static final DaoHelper daoHelper = UtilCreator.INSTANCE.getDefaultInstance(DaoHelper.class);
    @Autowired
    private ArticleDao articleDao;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private CategoryServiceImpl categoryService;
    @Autowired
    private CacheServiceImpl cacheService;
    @Autowired
    private RefreshElasticSearchServiceImpl refreshElasticSearchService;
    private static final Collection<ColumnInfo> forUpdate = new ArrayList<>();

    static {
        forUpdate.add(new ColumnInfo(true, false, "configuration", null));
        forUpdate.add(new ColumnInfo(true, false, "cid", null).toStringColumn(1, 50));
        forUpdate.add(new ColumnInfo(true, false, "title", null).toStringColumn(1, 500));
        forUpdate.add(new ColumnInfo(true, true, "imageSrc", null).toStringColumn(0, 1000));
        forUpdate.add(new ColumnInfo(true, true, "summary", null).toStringColumn(0, 3000));
        forUpdate.add(new ColumnInfo(true, true, "content", null).toStringColumn(0, Integer.MAX_VALUE));
        forUpdate.add(new ColumnInfo(true, false, "orderGlobal", null, Integer.MIN_VALUE, Integer.MAX_VALUE));
        forUpdate.add(new ColumnInfo(true, false, "orderCategory", null, Integer.MIN_VALUE, Integer.MAX_VALUE));
        forUpdate.add(new ColumnInfo(true, false, "articleState", null).toStringColumn(0, 50));
    }

    @Transactional
    public Article createArticle(ArticleDto articleDto) {
        parameterHelper.stringNotEmpty("cid", (Object) articleDto.getCid());
        parameterHelper.stringNotEmpty("title", (Object) articleDto.getTitle());
        parameterHelper.stringNotEmpty("imageSrc", (Object) articleDto.getImageSrc());

        ArticleConfiguration articleConfiguration = articleDto.getConfiguration();
        articleConfigurationValidate(articleConfiguration);

        HyggeRequestContext context = HyggeRequestTracker.getContext();
        User currentUser = context.getCurrentLoginUser();
        userService.checkUserRight(currentUser, UserTypeEnum.ROOT);

        nameConflictCheck(articleDto.getTitle());

        Article article = PoDtoMapper.INSTANCE.dtoToPo(articleDto);
        article.setWordCount(article.getContent() == null ? 0 : article.getContent().length());
        article.setUserId(currentUser.getUserId());
        article.setAid(randomHelper.getUniversallyUniqueIdentifier(true));
        Category category = categoryService.findCategoryByCid(articleDto.getCid(), false);
        article.setCategoryId(category.getCategoryId());
        article.setOrderGlobal(parameterHelper.parseObjectOfNullable("orderGlobal", article.getOrderGlobal(), 0));
        article.setOrderCategory(parameterHelper.parseObjectOfNullable("orderCategory", article.getOrderCategory(), 0));


        Article result = articleDao.save(article);

        String aid = result.getAid();
        Integer articleId = result.getArticleId();
        CompletableFuture.runAsync(() -> {
            refreshElasticSearchService.freshSingleArticle(aid, articleId);
        }).exceptionally(e -> {
            log.error("刷新文章(" + article.getArticleId() + ") 模糊搜索数据 失败.", e);
            return null;
        });

        return result;
    }

    @Transactional
    public Article updateArticle(String aid, Map<String, Object> data) {
        parameterHelper.stringNotEmpty("cid", (Object) aid);

        HyggeRequestContext context = HyggeRequestTracker.getContext();
        User currentUser = context.getCurrentLoginUser();
        userService.checkUserRight(currentUser, UserTypeEnum.ROOT);

        HashMap<String, Object> finalData = daoHelper.filterOutTheFinalColumns(data, forUpdate);

        Article old = articleDao.findArticleByAid(aid);

        Article newOne = MapToAnyMapper.INSTANCE.mapToArticle(finalData);

        String title = (String) finalData.get("title");
        if (title != null && !old.getTitle().equals(newOne.getTitle())) {
            nameConflictCheck(title);
        }
        String cid = (String) finalData.get("cid");
        if (cid != null) {
            Category category = categoryService.findCategoryByCid(cid, false);
            newOne.setCategoryId(category.getCategoryId());
        }
        if (finalData.containsKey("content")) {
            newOne.setWordCount(newOne.getContent() == null ? 0 : newOne.getContent().length());
        }
        if (newOne.getConfiguration() != null) {
            articleConfigurationValidate(newOne.getConfiguration());
        }

        OverrideMapper.INSTANCE.overrideToAnother(newOne, old);

        Article result = articleDao.save(old);

        Integer articleId = result.getArticleId();
        CompletableFuture.runAsync(() -> {
            refreshElasticSearchService.freshSingleArticle(aid, articleId);
        }).exceptionally(e -> {
            log.error("刷新文章(" + result.getArticleId() + ") 模糊搜索数据 失败.", e);
            return null;
        });

        return result;
    }

    public List<ArticleCountInfo> findArticleCountInfo(List<Integer> accessibleCategoryIdList, Integer currentUserId) {
        return currentUserId == null ? articleDao.findArticleCountsOfCategory(accessibleCategoryIdList)
                : articleDao.findArticleCountsOfCategory(accessibleCategoryIdList, currentUserId);
    }

    public ArticleCountInfo findArticleCountInfo(Integer categoryId, Integer currentUserId) {
        parameterHelper.integerFormatNotEmpty("categoryId", categoryId);
        List<Integer> accessibleCategoryIdList = collectionHelper.createCollection(categoryId);

        List<ArticleCountInfo> articleCountInfoList = findArticleCountInfo(accessibleCategoryIdList, currentUserId);
        if (parameterHelper.isEmpty(articleCountInfoList)) {
            return null;
        }
        return articleCountInfoList.get(0);
    }

    public ArticleSummaryInfo findArticleSummaryInfoByCategoryId(List<Integer> accessibleCategoryIdList, List<Category> accessibleCategoryList, Integer currentUserId, int currentPage, int pageSize) {
        ArticleSummaryInfo result = new ArticleSummaryInfo();
        if (accessibleCategoryIdList.isEmpty()) {
            result.setArticleSummaryList(new ArrayList<>(0));
            return result;
        }

        Page<Article> articleListTemp;
        Sort sort;
        if (accessibleCategoryIdList.size() != 1) {
            sort = Sort.by(Sort.Order.desc("orderGlobal"), Sort.Order.desc("createTs"));
        } else {
            sort = Sort.by(Sort.Order.desc("orderCategory"), Sort.Order.desc("createTs"));
        }

        Pageable pageable = PageRequest.of(currentPage - 1, pageSize, sort);

        if (currentUserId != null) {
            articleListTemp = articleDao.findArticleSummary(accessibleCategoryIdList, currentUserId, pageable);
        } else {
            articleListTemp = articleDao.findArticleSummary(accessibleCategoryIdList, pageable);
        }

        long totalCount = articleListTemp.getTotalElements();

        List<ArticleDto> articleSummaryList = collectionHelper.filterNonemptyItemAsArrayList(false, articleListTemp.getContent(), (item -> {
            ArticleDto articleDto = PoDtoMapper.INSTANCE.poToDto(item);
            if (accessibleCategoryList != null) {
                accessibleCategoryList.stream()
                        .filter(category -> category.getCategoryId().equals(item.getCategoryId()))
                        .findFirst()
                        .ifPresent(category -> articleDto.setCid(category.getCid()));
            }
            return articleDto;
        }));

        if (articleSummaryList != null && !articleSummaryList.isEmpty()) {
            // 为全部文章摘要构建类别树
            for (ArticleDto articleDto : articleSummaryList) {
                // 查询出的文章摘要都是允许访问类别下的，所以不会空指针
                Category category = accessibleCategoryList.stream().filter(item -> item.getCid().equals(articleDto.getCid())).findFirst().get();

                CategoryTreeInfo categoryTreeInfo = cacheService.getCategoryTreeFormCurrent(category.getCategoryId());
                articleDto.setCategoryTreeInfo(categoryTreeInfo);
            }
        }
        result.setArticleSummaryList(articleSummaryList);
        result.setTotalCount(totalCount);
        return result;
    }

    public ArticleDto findArticleDetailByAid(boolean pageViewsIncrease, String aid) {
        parameterHelper.stringNotEmpty("aid", (Object) aid);

        Article article = articleDao.findArticleByAid(aid);
        if (article == null) {
            return null;
        }

        HyggeRequestContext context = HyggeRequestTracker.getContext();
        User currentUser = context.getCurrentLoginUser();

        List<Category> categoryList = categoryService.getAccessibleCategoryList(currentUser, null);

        Category currentCategory = categoryList.stream().filter(item -> item.getCategoryId().equals(article.getCategoryId())).findFirst().orElse(null);
        if (currentCategory == null) {
            // 当前用户无权访问
            return null;
        }

        ArticleDto result = PoDtoMapper.INSTANCE.poToDto(article);
        result.setCid(currentCategory.getCid());

        CategoryTreeInfo categoryTreeInfo = cacheService.getCategoryTreeFormCurrent(currentCategory.getCategoryId());
        result.setCategoryTreeInfo(categoryTreeInfo);

        // 如果允许更新浏览量
        if (pageViewsIncrease) {
            // 文章作者自己
            if (article.getUserId()
                    .equals(Optional.ofNullable(currentUser)
                            .map(User::getUserId)
                            .orElse(null))) {
                CompletableFuture.runAsync(() -> {
                    articleDao.increaseSelfView(article.getArticleId());
                }).exceptionally(e -> {
                    log.error("更新文章(" + article.getArticleId() + ") 自浏览 失败.", e);
                    return null;
                });
            } else {
                // 不是文章作者自己
                CompletableFuture.runAsync(() -> {
                    articleDao.increasePageViews(article.getArticleId());
                }).exceptionally(e -> {
                    log.error("更新文章(" + article.getArticleId() + ") 浏览量 失败.", e);
                    return null;
                });
            }
        }

        return result;
    }

    private void articleConfigurationValidate(ArticleConfiguration articleConfiguration) {
        if (articleConfiguration != null) {
            articleConfiguration.setBackgroundMusicType(parameterHelper.parseObjectOfNullable("backgroundMusicType", articleConfiguration.getBackgroundMusicType(), BackgroundMusicTypeEnum.NONE));
            articleConfiguration.setMediaPlayType(parameterHelper.parseObjectOfNullable("mediaPlayType", articleConfiguration.getMediaPlayType(), MediaPlayTypeEnum.SUGGEST_AUTO_PLAY));

            if (!BackgroundMusicTypeEnum.NONE.equals(articleConfiguration.getBackgroundMusicType())) {
                parameterHelper.stringNotEmpty("src", (Object) articleConfiguration.getSrc());
            }

            if (BackgroundMusicTypeEnum.DEFAULT.equals(articleConfiguration.getBackgroundMusicType())) {
                parameterHelper.stringNotEmpty("name", (Object) articleConfiguration.getName());
                parameterHelper.stringNotEmpty("artist", (Object) articleConfiguration.getArtist());
            }
        }
    }

    public void nameConflictCheck(String title) {
        Article old = articleDao.findArticleByTitle(title);
        if (old != null) {
            throw new LightRuntimeException(String.format("Article(%s) already exists.", title), BlogSystemCode.ARTICLE_ALREADY_EXISTS);
        }
    }
}
