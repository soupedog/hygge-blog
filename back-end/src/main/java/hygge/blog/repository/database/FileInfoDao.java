package hygge.blog.repository.database;

import hygge.blog.domain.local.po.FileInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Xavier
 * @date 2024/9/12
 */
@Repository
public interface FileInfoDao extends JpaRepository<FileInfo, Integer> {
}
