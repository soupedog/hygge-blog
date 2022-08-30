package hygge.blog.dao;

import hygge.blog.domain.po.Article;
import hygge.blog.domain.po.ArticleCountInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Xavier
 * @date 2022/7/20
 */
@Repository
public interface ArticleDao extends JpaRepository<Article, Integer> {

    Article findArticleByTitle(String title);

    @Modifying
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Query(value = "update article set pageViews=pageViews+1 where articleId=:articleId", nativeQuery = true)
    int increasePageViews(@Param("articleId") int articleId);

    @Modifying
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Query(value = "update article set pageViews=pageViews+1,selfPageViews=selfPageViews+1 where articleId=:articleId", nativeQuery = true)
    int increasePageViewsAndSelfView(@Param("articleId") int articleId);

    @Modifying
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Query(value = "update article set selfPageViews=selfPageViews+1 where articleId=:articleId", nativeQuery = true)
    int increaseSelfView(@Param("articleId") int articleId);

    Article findArticleByAid(String aid);

    @Query(countQuery = "select count(*) from article where categoryId in :accessibleCategoryList and (articleState='ACTIVE' or userId=:userId) ",
            value = "select null as configuration,null as content,createTs,lastUpdateTs,articleId,aid,categoryId,userId,title,imageSrc,summary,wordCount,pageViews,selfPageViews,orderGlobal,orderCategory,articleState from article where categoryId in :accessibleCategoryList and (articleState='ACTIVE' or userId=:userId)", nativeQuery = true)
    Page<Article> findArticleSummary(@Param("accessibleCategoryList") List<Integer> accessibleCategoryList, @Param("userId") Integer userId, Pageable pageable);

    @Query(countQuery = "select count(*) from article where categoryId in :accessibleCategoryList and articleState='ACTIVE' ",
            value = "select null as configuration,null as content,createTs,lastUpdateTs,articleId,aid,categoryId,userId,title,imageSrc,summary,wordCount,pageViews,selfPageViews,orderGlobal,orderCategory,articleState from article where categoryId in :accessibleCategoryList and articleState='ACTIVE' ", nativeQuery = true)
    Page<Article> findArticleSummary(@Param("accessibleCategoryList") List<Integer> accessibleCategoryList, Pageable pageable);

    @Query(value = "select new hygge.blog.domain.po.ArticleCountInfo(categoryId, count(articleId)) from Article where categoryId in :accessibleCategoryList and articleState='ACTIVE' group by categoryId")
    List<ArticleCountInfo> findArticleCountsOfCategory(@Param("accessibleCategoryList") List<Integer> accessibleCategoryList);

    @Query(value = "select new hygge.blog.domain.po.ArticleCountInfo(categoryId, count(articleId)) from Article where categoryId in :accessibleCategoryList and (articleState='ACTIVE' or userId=:userId) group by categoryId")
    List<ArticleCountInfo> findArticleCountsOfCategory(@Param("accessibleCategoryList") List<Integer> accessibleCategoryList, @Param("userId") Integer userId);
}
