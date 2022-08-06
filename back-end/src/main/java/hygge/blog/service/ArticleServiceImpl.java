package hygge.blog.service;

import hygge.blog.common.HyggeRequestContext;
import hygge.blog.common.HyggeRequestTracker;
import hygge.blog.dao.ArticleDao;
import hygge.blog.domain.bo.BlogSystemCode;
import hygge.blog.domain.dto.ArticleDto;
import hygge.blog.domain.dto.TopicDto;
import hygge.blog.domain.dto.inner.ArticleSummaryInfo;
import hygge.blog.domain.dto.inner.CategoryTreeInfo;
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
import hygge.commons.enums.ColumnTypeEnum;
import hygge.commons.exceptions.LightRuntimeException;
import hygge.utils.UtilsCreator;
import hygge.utils.bo.ColumnInfo;
import hygge.utils.definitions.DaoHelper;
import hygge.web.template.HyggeWebUtilContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Xavier
 * @date 2022/7/24
 */
@Service
public class ArticleServiceImpl extends HyggeWebUtilContainer {
    private static final DaoHelper daoHelper = UtilsCreator.INSTANCE.getDefaultInstance(DaoHelper.class);
    @Autowired
    private ArticleDao articleDao;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private CategoryServiceImpl categoryService;
    @Autowired
    private TopicServiceImpl topicService;
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

        return articleDao.save(article);
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
        if (title != null) {
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

        return articleDao.save(old);
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

    public ArticleSummaryInfo findArticleSummaryInfoByCategoryId(List<Integer> accessibleCategoryIdList, List<Category> categoryList, Integer currentUserId, int currentPage, int pageSize) {
        ArticleSummaryInfo result = new ArticleSummaryInfo();
        if (accessibleCategoryIdList.isEmpty()) {
            result.setArticleSummaryList(new ArrayList<>(0));
            return result;
        }

        List<Article> articleList;
        int totalCount;
        if (currentUserId != null) {
            articleList = articleDao.findArticleSummary(accessibleCategoryIdList, currentUserId, "orderGlobal desc,createTs desc", (currentPage - 1) * pageSize, pageSize);
            totalCount = articleDao.findArticleSummaryTotalCount(accessibleCategoryIdList, currentUserId);
        } else {
            articleList = articleDao.findArticleSummary(accessibleCategoryIdList, "orderGlobal desc,createTs desc", (currentPage - 1) * pageSize, pageSize);
            totalCount = articleDao.findArticleSummaryTotalCount(accessibleCategoryIdList);
        }

        List<ArticleDto> articleSummaryList = collectionHelper.filterNonemptyItemAsArrayList(false, articleList, (item -> {
            ArticleDto articleDto = PoDtoMapper.INSTANCE.poToDto(item);
            if (categoryList != null) {
                categoryList.stream()
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

            for (ArticleDto articleDto : articleSummaryList) {
                // 确认不会空指针
                Category category = categoryList.stream().filter(item -> item.getCid().equals(articleDto.getCid())).findFirst().get();

                if (topic == null) {
                    topic = topicService.findTopicByTopicId(category.getTopicId(), false);
                }
                if (topicDto == null) {
                    topicDto = PoDtoMapper.INSTANCE.poToDto(topic);
                }

                initCategoryTreeInfo(topicDto, category, categoryList, articleDto);
            }
        }
        result.setArticleSummaryList(articleSummaryList);
        result.setTotalCount(totalCount);
        return result;
    }

    public ArticleDto findArticleDetailByAid(String aid) {
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

        initCategoryTreeInfo(PoDtoMapper.INSTANCE.poToDto(currentTopic), currentCategory, categoryList, result);

        return result;
    }

    private void initCategoryTreeInfo(TopicDto currentTopicDto, Category currentCategory, List<Category> categoryList, ArticleDto result) {
        CategoryTreeInfo categoryTreeInfo = new CategoryTreeInfo();
        categoryTreeInfo.setTopicInfo(currentTopicDto);
        categoryTreeInfo.setCategoryList(new ArrayList<>(0));

        // 确保当前节点一定被添加
        while (categoryTreeInfo.getCategoryList().isEmpty() || (currentCategory != null && currentCategory.getParentId() != null)) {
            categoryTreeInfo.getCategoryList().add(PoDtoMapper.INSTANCE.poToDto(currentCategory));
            // 确认不会空指针
            Integer parentId = currentCategory.getParentId();
            currentCategory = categoryList.stream().filter(item -> item.getCategoryId().equals(parentId)).findFirst().orElse(null);
        }

        // 上面是从当前找到根节点，所以需要反转数组才是从根到当前节点
        Collections.reverse(categoryTreeInfo.getCategoryList());
        result.setCategoryTreeInfo(categoryTreeInfo);
    }

    private void articleConfigurationValidate(ArticleConfiguration articleConfiguration) {
        if (articleConfiguration != null) {
            articleConfiguration.setBackgroundMusicType(parameterHelper.parseObjectOfNullable("backgroundMusicType", articleConfiguration.getBackgroundMusicType(), BackgroundMusicTypeEnum.NONE));
            articleConfiguration.setMediaPlayType(parameterHelper.parseObjectOfNullable("mediaPlayType", articleConfiguration.getMediaPlayType(), MediaPlayTypeEnum.SUGGEST_AUTO_PLAY));
            parameterHelper.stringNotEmpty("src", (Object) articleConfiguration.getSrc());
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
