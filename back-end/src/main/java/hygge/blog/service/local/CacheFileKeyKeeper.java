package hygge.blog.service.local;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import hygge.commons.constant.enums.StringCategoryEnum;
import hygge.util.template.HyggeJsonUtilContainer;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * 基于 {@link Cache} 实现的 {@link FileKeyKeeper}
 *
 * @author Xavier
 * @date 2025/11/10
 */
@Service
public class CacheFileKeyKeeper extends HyggeJsonUtilContainer implements FileKeyKeeper {
    private final Cache<String, FileKeyLocalCounter> fileKeyCache = CacheBuilder.newBuilder()
            .initialCapacity(200)
            .maximumSize(6666L)
            // 根据硬件条件动态设置并发
            .concurrencyLevel(Runtime.getRuntime().availableProcessors())
            // 1 分钟失效
            .expireAfterAccess(Duration.ofMinutes(1L))
            .build();

    @Override
    public String genderKey(String fileNo) {
        return randomHelper.getRandomString(6, StringCategoryEnum.A_Z, StringCategoryEnum.a_z, StringCategoryEnum.NUMBER);
    }

    /**
     * 同步方法，防止键重复
     */
    @Override
    public synchronized String createFileKey(String fileNo) {
        String fileKey = genderKey(fileNo);

        while (fileKeyCache.getIfPresent(fileKey) != null) {
            fileKey = genderKey(fileNo);
        }

        fileKeyCache.put(fileKey, new FileKeyLocalCounter(fileNo));
        return fileKey;
    }

    @Override
    public boolean writeOffFileKey(String fileNo, String fileKey) {
        FileKeyLocalCounter counter = fileKeyCache.getIfPresent(fileKey);
        if (counter == null) {
            return false;
        }

        return counter.writeOff();
    }


}
