package hygge.blog.repository.database;

import hygge.blog.domain.po.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Xavier
 * @date 2022/7/23
 */
@Repository
public interface TopicDao extends JpaRepository<Topic, Integer> {

    Topic findTopicByTopicName(String TopicName);

    Topic findTopicByTid(String tid);
}
