package hygge.blog.util;

import hygge.blog.repository.elasticsearch.SearchingCacheDao;
import hygge.blog.service.elasticsearch.RefreshElasticSearchServiceImpl;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

/**
 * 模拟本地工具不需要真实加载的类
 *
 * @author Xavier
 * @date 2022/9/27
 */
@Configuration
public class MyMockBean {
    @Bean
    public RefreshElasticSearchServiceImpl refreshElasticSearchService() {
        return Mockito.mock(RefreshElasticSearchServiceImpl.class);
    }

    @Bean
    public SearchingCacheDao searchingCacheDao() {
        return Mockito.mock(SearchingCacheDao.class);
    }

    @Bean
    public ElasticsearchOperations operations() {
        return Mockito.mock(ElasticsearchOperations.class);
    }
}
