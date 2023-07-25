package hygge.blog.repository.elasticsearch;

import hygge.blog.domain.local.dto.ArticleQuoteSearchCache;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SearchingCacheDao extends CrudRepository<ArticleQuoteSearchCache, Integer> {
}
