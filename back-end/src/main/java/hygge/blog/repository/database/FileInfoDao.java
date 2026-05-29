package hygge.blog.repository.database;

import hygge.blog.domain.local.enums.FileCacheTypeEnum;
import hygge.blog.domain.local.po.FileInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Modifying
    @Transactional
    @Query(value = "update file_info set fileCacheType = :#{#fileCacheType.getValue()}, description = JSON_SET(COALESCE(description, '{}'), '$.nginxLink', :cacheLink) where fileNo = :fileNo", nativeQuery = true)
    int updateFileCacheLink(@Param("fileNo") String fileNo, @Param("fileCacheType") FileCacheTypeEnum fileCacheType, @Param("cacheLink") String cacheLink);

    @Modifying
    @Transactional
    @Query(value = "update file_info set fileCacheType = 'DEFAULT', description = JSON_REMOVE(COALESCE(description, '{}'), '$.nginxLink') where fileNo = :fileNo", nativeQuery = true)
    int removeFileCacheLink(@Param("fileNo") String fileNo);
}
