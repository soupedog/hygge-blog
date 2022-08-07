package hygge.blog.dao;

import hygge.blog.domain.po.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Xavier
 * @date 2022/8/7
 */
@Repository
public interface AnnouncementDao extends JpaRepository<Announcement, Integer> {

}
