package hygge.blog.repository.database;

import hygge.blog.domain.local.enums.FileTypeEnum;
import hygge.blog.domain.local.po.view.FileInfoView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;

/**
 * @author Xavier
 * @date 2025/10/28
 */
@Repository
public interface FileInfoViewDao extends JpaRepository<FileInfoView, Integer> {
    /**
     * 判断文件是否已存在
     *
     * @return Integer fileId
     */
    boolean existsByName(String name);

    /**
     * 判断文件是否已存在
     *
     * @return Integer fileId
     */
    boolean existsByFileTypeAndNameAndExtension(FileTypeEnum fileType, String name, String extension);

    @Query(value = "from FileInfoView where fileType in :fileTypeCollection and (cid in :cidCollection or cid is null)")
    Page<FileInfoView> findFileInfoMultiple(@Param("fileTypeCollection") Collection<FileTypeEnum> fileTypeCollection, @Param("cidCollection") Collection<String> cidCollection, Pageable pageable);

    /**
     * @param keywordPattern 需要手工添加符号如 "%张三%", 才代表要求名称包含张三
     */
    @Query(value = "from FileInfoView where (cid in :cidCollection or cid is null) and name like :keywordPattern")
    Page<FileInfoView> findFileInfoByNameMultiple(@Param("keywordPattern") String keywordPattern, @Param("cidCollection") Collection<String> cidCollection, Pageable pageable);
}
