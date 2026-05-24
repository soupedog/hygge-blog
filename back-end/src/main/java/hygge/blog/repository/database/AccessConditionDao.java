package hygge.blog.repository.database;

import hygge.blog.domain.local.po.AccessCondition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * @author Xavier
 * @date 2026/5/24
 */
@Repository
public interface AccessConditionDao extends JpaRepository<AccessCondition, Integer> {
    List<AccessCondition> findAccessConditionsByAcIdIn(Collection<Integer> acIdCollection);
}
