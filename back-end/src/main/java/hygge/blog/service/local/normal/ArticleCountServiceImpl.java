package hygge.blog.service.local.normal;

import hygge.blog.domain.local.po.ArticleCountInfo;
import hygge.blog.repository.database.ArticleDao;
import hygge.util.template.HyggeJsonUtilContainer;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Xavier
 * @date 2025/8/28
 */
@Service
public class ArticleCountServiceImpl extends HyggeJsonUtilContainer {
    private final ArticleDao articleDao;

    public ArticleCountServiceImpl(ArticleDao articleDao) {
        this.articleDao = articleDao;
    }

    public List<ArticleCountInfo> findArticleCountInfoOfCategory(List<Integer> accessibleCategoryIdList, Integer currentUserId) {
        return currentUserId == null ? articleDao.findArticleCountsOfCategory(accessibleCategoryIdList)
                : articleDao.findArticleCountsOfCategory(accessibleCategoryIdList, currentUserId);
    }

    public ArticleCountInfo findArticleCountInfoOfCategory(Integer categoryId, Integer currentUserId) {
        parameterHelper.integerFormatNotEmpty("categoryId", categoryId);
        List<Integer> accessibleCategoryIdList = collectionHelper.createCollection(categoryId);

        List<ArticleCountInfo> articleCountInfoList = findArticleCountInfoOfCategory(accessibleCategoryIdList, currentUserId);
        if (parameterHelper.isEmpty(articleCountInfoList)) {
            return null;
        }
        return articleCountInfoList.get(0);
    }
}
