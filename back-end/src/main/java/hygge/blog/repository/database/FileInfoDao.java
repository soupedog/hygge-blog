package hygge.blog.repository.database;

import hygge.blog.domain.local.enums.FileTypeEnum;
import hygge.blog.domain.local.po.FileInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;

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

    @Query(value = "from FileInfo where fileType in :fileTypeCollection and (cid in :cidCollection or cid is null)")
    Page<FileInfo> findFileInfoMultiple(@Param("fileTypeCollection") Collection<FileTypeEnum> fileTypeCollection, @Param("cidCollection") Collection<String> cidCollection, Pageable pageable);

    /**
     * @param keywordPattern 需要手工添加符号如 "%张三%", 才代表要求名称包含张三
     */
    @Query(value = "from FileInfo where (cid in :cidCollection or cid is null) and name like :keywordPattern")
    Page<FileInfo> findFileInfoByNameMultiple(@Param("keywordPattern") String keywordPattern, @Param("cidCollection") Collection<String> cidCollection, Pageable pageable);
}
