package hygge.blog.repository.database;

import hygge.blog.domain.local.po.ArticleBrowseLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * @author Xavier
 * @date 2022/11/19
 */
@Repository
public interface ArticleBrowseLogDao extends JpaRepository<ArticleBrowseLog, Integer> {
    @Query(value = "SELECT ip FROM article_browse_log WHERE ipLocation IS NULL GROUP BY ip limit 1", nativeQuery = true)
    String findAnIpWithoutLocation();

    @Query(value = "SELECT * FROM article_browse_log WHERE ip=:ip AND ipLocation IS NOT NULL AND latitude IS NOT NULL AND longitude IS NOT NULL limit 1", nativeQuery = true)
    ArticleBrowseLog findIpLocationInfoFromLocal(@Param("ip") String ip);

    @Modifying
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Query(value = "UPDATE article_browse_log SET ipLocation=:ipLocation,latitude=:latitude,longitude=:longitude,lastUpdateTs=:currentTimeStamp WHERE ip=:ip AND (ipLocation IS NULL OR (latitude IS NULL OR longitude IS NULL))", nativeQuery = true)
    int updateIpLocationInfoForAll(@Param("ip") String ip, @Param("latitude") String latitude, @Param("longitude") String longitude, @Param("ipLocation") String ipLocation, @Param("currentTimeStamp") Timestamp currentTimeStamp);

    @Query(value = "SELECT * FROM article_browse_log WHERE ip=:ip AND (ipLocation IS NULL OR (latitude IS NULL OR longitude IS NULL))", nativeQuery = true)
    ArrayList<ArticleBrowseLog> findSameIpAndMissingDataTargetList(@Param("ip") String ip);
}
