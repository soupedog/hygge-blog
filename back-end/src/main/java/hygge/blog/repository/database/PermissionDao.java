package hygge.blog.repository.database;

import hygge.blog.domain.local.po.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Xavier
 * @date 2026/5/24
 */
@Repository
public interface PermissionDao extends JpaRepository<Permission, Integer> {
}
