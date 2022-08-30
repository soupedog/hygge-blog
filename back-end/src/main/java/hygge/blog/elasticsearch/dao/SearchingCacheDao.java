package hygge.blog.elasticsearch.dao;

import hygge.blog.elasticsearch.dto.FuzzySearchCache;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SearchingCacheDao extends CrudRepository<FuzzySearchCache, Integer> {
}
