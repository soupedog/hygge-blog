package hygge.blog.repository.elasticsearch;

import hygge.blog.domain.local.dto.ArticleQuoteSearchCache;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SearchingCacheDao extends ElasticsearchRepository<ArticleQuoteSearchCache, Integer> {
}
