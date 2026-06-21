package hygge.blog.config.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

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
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        // 设置默认的缓存策略：写入后10分钟过期，最大容量1000
        Caffeine<Object, Object> caffeine = Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.HOURS)
                .maximumSize(1000);
        cacheManager.setCaffeine(caffeine);
        return new ConcurrentMapCacheManager();
    }
}
