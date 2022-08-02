package hygge.blog.dao;

import hygge.blog.domain.po.Quote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Xavier
 * @date 2022/8/2
 */
@Repository
public interface QuoteDao extends JpaRepository<Quote, Integer> {
}
