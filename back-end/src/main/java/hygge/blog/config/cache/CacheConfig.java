package hygge.blog.config.cache;

import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Xavier
 * @date 2023/7/26
 */
@Configuration
public class CacheConfig {
    /**
     * 减少外部中间件，用内存版缓存
     */
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager();
    }
}
