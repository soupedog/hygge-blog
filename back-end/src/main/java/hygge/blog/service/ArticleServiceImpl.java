package hygge.blog.service;

import hygge.blog.common.HyggeRequestContext;
import hygge.blog.common.HyggeRequestTracker;
import hygge.blog.dao.ArticleDao;
import hygge.blog.domain.bo.BlogSystemCode;
import hygge.blog.domain.dto.ArticleDto;
import hygge.blog.domain.dto.TopicDto;
import hygge.blog.domain.dto.inner.ArticleSummaryInfo;
import hygge.blog.domain.enums.BackgroundMusicTypeEnum;
import hygge.blog.domain.enums.MediaPlayTypeEnum;
import hygge.blog.domain.enums.UserTypeEnum;
import hygge.blog.domain.mapper.MapToAnyMapper;
import hygge.blog.domain.mapper.OverrideMapper;
import hygge.blog.domain.mapper.PoDtoMapper;
import hygge.blog.domain.po.Article;
import hygge.blog.domain.po.ArticleCountInfo;
import hygge.blog.domain.po.Category;
import hygge.blog.domain.po.Topic;
import hygge.blog.domain.po.User;
import hygge.blog.domain.po.inner.ArticleConfiguration;
import hygge.blog.elasticsearch.service.RefreshElasticSearchServiceImpl;
import hygge.commons.constant.enums.ColumnTypeEnum;
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
    private TopicServiceImpl topicService;
    @Autowired
    private RefreshElasticSearchServiceImpl refreshElasticSearchService;
    private static final Collection<ColumnInfo> forUpdate = new ArrayList<>();

    static {
        forUpdate.add(new ColumnInfo("configuration", null, ColumnTypeEnum.OTHER_OBJECT, true, false, 0, 0));
        forUpdate.add(new ColumnInfo("cid", null, ColumnTypeEnum.STRING, true, false, 1, 50));
        forUpdate.add(new ColumnInfo("title", null, ColumnTypeEnum.STRING, true, false, 1, 500));
        forUpdate.add(new ColumnInfo("imageSrc", null, ColumnTypeEnum.STRING, true, true, 0, 1000));
        forUpdate.add(new ColumnInfo("summary", null, ColumnTypeEnum.STRING, true, true, 0, 3000));
        forUpdate.add(new ColumnInfo("content", null, ColumnTypeEnum.STRING, true, true, 0, Integer.MAX_VALUE));
        forUpdate.add(new ColumnInfo("orderGlobal", null, ColumnTypeEnum.INTEGER, true, false, 0, Integer.MAX_VALUE));
        forUpdate.add(new ColumnInfo("orderCategory", null, ColumnTypeEnum.INTEGER, true, false, 0, Integer.MAX_VALUE));
        forUpdate.add(new ColumnInfo("articleState", null, ColumnTypeEnum.STRING, true, false, 1, 50));
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
            // 构建类别树严格来说前端做合适
            // 已确定不会出现多 topic 的情景
            Topic topic = null;
            TopicDto topicDto = null;

            List<Category> allCategoryList = categoryService.initRootCategory(accessibleCategoryList);

            for (ArticleDto articleDto : articleSummaryList) {
                // 确认不会空指针
                Category category = accessibleCategoryList.stream().filter(item -> item.getCid().equals(articleDto.getCid())).findFirst().get();

                if (topic == null) {
                    topic = topicService.findTopicByTopicId(category.getTopicId(), false);
                }
                if (topicDto == null) {
                    topicDto = PoDtoMapper.INSTANCE.poToDto(topic);
                }

                articleDto.initCategoryTreeInfo(topicDto, category, allCategoryList);
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

        Topic currentTopic = topicService.findTopicByTopicId(currentCategory.getTopicId(), false);

        ArticleDto result = PoDtoMapper.INSTANCE.poToDto(article);
        result.setCid(currentCategory.getCid());

        result.initCategoryTreeInfo(PoDtoMapper.INSTANCE.poToDto(currentTopic), currentCategory, categoryList);

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

            if (!articleConfiguration.getBackgroundMusicType().equals(BackgroundMusicTypeEnum.NONE)) {
                parameterHelper.stringNotEmpty("src", (Object) articleConfiguration.getSrc());
            } else if (BackgroundMusicTypeEnum.DEFAULT.equals(articleConfiguration.getBackgroundMusicType())) {
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
