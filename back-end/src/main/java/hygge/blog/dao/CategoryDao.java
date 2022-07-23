package hygge.blog.dao;

import hygge.blog.domain.po.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Xavier
 * @date 2022/7/23
 */
@Repository
public interface CategoryDao extends JpaRepository<Category, Integer> {
    Category findCategoryByCid(String cid);

    Category findCategoryByCategoryName(String categoryName);
}
