package hygge.blog.repository.database;

import hygge.blog.domain.local.po.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * @author Xavier
 * @date 2022/7/23
 */
@Repository
public interface CategoryDao extends JpaRepository<Category, Integer> {
    Category findCategoryByCid(String cid);

    Category findCategoryByCategoryName(String categoryName);


    @Query(value = "select * from category where categoryId in :categoryIdCollection", nativeQuery = true)
    List<Category> findCategoryByCategoryIdList(@Param("categoryIdCollection")Collection<Integer> categoryIdCollection);
}
