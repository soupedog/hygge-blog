package hygge.blog.dao;

import hygge.blog.domain.po.BlogGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Xavier
 * @date 2022/7/17
 */
@Repository
public interface BlogGroupDao extends JpaRepository<BlogGroup, Integer> {
    BlogGroup findBlogGroupByGid(String gid);

    BlogGroup findBlogGroupByGroupName(String groupName);

    @Modifying
    @Transactional
    @Query(value = "delete from join_user_blog_group where groupId=:groupId and userId=:userId", nativeQuery = true)
    int eviction(@Param("userId") Integer userId, @Param("groupId") Integer groupId);
}
