package hygge.blog.dao;

import hygge.blog.domain.po.Article;
import hygge.blog.domain.po.ArticleCountInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Xavier
 * @date 2022/7/20
 */
@Repository
public interface ArticleDao extends JpaRepository<Article, Integer> {

    Article findArticleByTitle(String title);

    Article findArticleByAid(String aid);

    @Query(value = "select null as articleState,null as configuration,null as content,createTs,lastUpdateTs,articleId,aid,categoryId,userId,title,imageSrc,summary,wordCount,pageViews,selfPageViews,orderGlobal,orderCategory from article where categoryId in :accessibleCategoryList or userId=:userId order by :orderInfo LIMIT :startPoint,:size ;", nativeQuery = true)
    List<Article> findArticleSummary(@Param("accessibleCategoryList") List<Integer> accessibleCategoryList, @Param("userId") Integer userId, @Param("orderInfo") String orderInfo, int startPoint, int size);

    @Query(value = "select null as articleState,null as configuration,null as content,createTs,lastUpdateTs,articleId,aid,categoryId,userId,title,imageSrc,summary,wordCount,pageViews,selfPageViews,orderGlobal,orderCategory from article where categoryId in :accessibleCategoryList order by :orderInfo LIMIT :startPoint,:size ;", nativeQuery = true)
    List<Article> findArticleSummary(@Param("accessibleCategoryList") List<Integer> accessibleCategoryList, @Param("orderInfo") String orderInfo, int startPoint, int size);

    @Query(value = "select count(*) from article where categoryId in :accessibleCategoryList or userId=:userId ;", nativeQuery = true)
    int findArticleSummaryTotalCount(@Param("accessibleCategoryList") List<Integer> accessibleCategoryList, @Param("userId") Integer userId);

    @Query(value = "select count(*) from article where categoryId in :accessibleCategoryList ;", nativeQuery = true)
    int findArticleSummaryTotalCount(@Param("accessibleCategoryList") List<Integer> accessibleCategoryList);

    @Query(value = "select new hygge.blog.domain.po.ArticleCountInfo(categoryId, count(articleId)) from Article where categoryId in :accessibleCategoryList group by categoryId")
    List<ArticleCountInfo> findArticleCountsOfCategory(@Param("accessibleCategoryList") List<Integer> accessibleCategoryList);

    @Query(value = "select new hygge.blog.domain.po.ArticleCountInfo(categoryId, count(articleId)) from Article where categoryId in :accessibleCategoryList or userId=:userId group by categoryId")
    List<ArticleCountInfo> findArticleCountsOfCategory(@Param("accessibleCategoryList") List<Integer> accessibleCategoryList, @Param("userId") Integer userId);
}
