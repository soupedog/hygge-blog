package hygge.blog.service;

import hygge.blog.common.HyggeRequestContext;
import hygge.blog.common.HyggeRequestTracker;
import hygge.blog.dao.ArticleDao;
import hygge.blog.domain.bo.BlogSystemCode;
import hygge.blog.domain.dto.ArticleDto;
import hygge.blog.domain.enums.BackgroundMusicTypeEnum;
import hygge.blog.domain.enums.MediaPlayTypeEnum;
import hygge.blog.domain.enums.UserTypeEnum;
import hygge.blog.domain.mapper.MapToAnyMapper;
import hygge.blog.domain.mapper.OverrideMapper;
import hygge.blog.domain.mapper.PoDtoMapper;
import hygge.blog.domain.po.Article;
import hygge.blog.domain.po.ArticleCountInfo;
import hygge.blog.domain.po.Category;
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

    public List<ArticleCountInfo> findArticleCountInfo(List<Integer> accessibleCategoryIdList, Integer userId) {
        return userId == null ? articleDao.findArticleCountsOfCategory(accessibleCategoryIdList)
                : articleDao.findArticleCountsOfCategory(accessibleCategoryIdList, userId);
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
