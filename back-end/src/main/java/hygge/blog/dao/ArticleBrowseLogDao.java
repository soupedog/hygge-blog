package hygge.blog.dao;

import hygge.blog.domain.po.ArticleBrowseLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Xavier
 * @date 2022/11/19
 */
@Repository
public interface ArticleBrowseLogDao extends JpaRepository<ArticleBrowseLog, Integer> {
    @Query(value = "select ip from article_browse_log where ipLocation is null group by ip limit 1", nativeQuery = true)
    String findAnIpWithoutLocation();

    @Query(value = "select ipLocation from article_browse_log where ip=:ip and ipLocation is not null limit 1", nativeQuery = true)
    String findIpLocationFromLocal(@Param("ip") String ip);

    @Modifying
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Query(value = "update article_browse_log set ipLocation=:ipLocation where ip=:ip", nativeQuery = true)
    int updateIpLocationForAll(@Param("ip") String ip, @Param("ipLocation") String ipLocation);
}
