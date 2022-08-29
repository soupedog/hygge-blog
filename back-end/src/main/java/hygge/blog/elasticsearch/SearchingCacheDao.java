package hygge.blog.elasticsearch;

import hygge.blog.elasticsearch.dto.FuzzySearchCache;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface SearchingCacheDao extends CrudRepository<FuzzySearchCache, Integer> {

    Page<FuzzySearchCache> findByTypeAndCategoryIdContains(FuzzySearchCache.Type type, List<Integer> s, Pageable pageable);
}
