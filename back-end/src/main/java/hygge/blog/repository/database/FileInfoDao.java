package hygge.blog.repository.database;

import hygge.blog.domain.local.po.FileInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Xavier
 * @date 2024/9/12
 */
@Repository
public interface FileInfoDao extends JpaRepository<FileInfo, Integer> {
    /**
     * 根据文件名称判断是否已存在
     *
     * @return Integer fileId
     */
    boolean existsByName(String name);

    @Modifying
    @Transactional
    long deleteByFileNo(String fileNo);
}
