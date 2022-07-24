package hygge.blog.dao;

import hygge.blog.domain.po.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Xavier
 * @date 2022/7/20
 */
@Repository
public interface ArticleDao extends JpaRepository<Article, Integer> {

    Article findArticleByTitle(String title);

    Article findArticleByAid(String aid);
}
