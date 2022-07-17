package hygge.blog.dao;

import hygge.blog.domain.po.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Xavier
 * @date 2022/7/17
 */
@Repository
public interface UserDao extends JpaRepository<User, Long> {
}
