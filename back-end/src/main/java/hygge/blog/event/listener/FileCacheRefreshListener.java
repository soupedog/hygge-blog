package hygge.blog.event.listener;

import hygge.blog.domain.local.bo.CacheObjectContainer;
import hygge.blog.event.FileCacheRefreshEvent;
import hygge.blog.event.FileCacheRefreshEventInfo;
import hygge.commons.exception.UnreachableRuntimeException;
import hygge.commons.spring.event.BaseHyggeEventListener;
import hygge.commons.spring.event.HyggeEventListenerContext;
import hygge.util.UtilCreator;
import hygge.util.definition.ParameterHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import static hygge.blog.domain.local.bo.CacheObjectContainer.CacheTypeEnum.FILE_NO_URL_MAPPING;

/**
 * @author Xavier
 * @date 2026/5/29
 */
@Slf4j
public class FileCacheRefreshListener extends BaseHyggeEventListener<FileCacheRefreshEventInfo, FileCacheRefreshEvent> {
    private static final ParameterHelper parameterHelper = UtilCreator.INSTANCE.getDefaultInstance(ParameterHelper.class);
    public static final CacheObjectContainer.CacheTypeEnum type = FILE_NO_URL_MAPPING;
    private final CacheManager cacheManager;

    public FileCacheRefreshListener(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    protected String getListenerName() {
        return this.getClass().getSimpleName();
    }

    @Override
    protected void handleEvent(HyggeEventListenerContext<FileCacheRefreshEventInfo, FileCacheRefreshEvent> context, FileCacheRefreshEvent event) {
        FileCacheRefreshEventInfo info = event.getActualSource();
        Cache cache = cacheManager.getCache(type.getValue());
        if (cache == null) {
            throw new UnreachableRuntimeException();
        }

        if (info.isForAll()) {
            cache.clear();
        } else {
            String fileNo = info.getFileNo();

            if (parameterHelper.isEmpty(fileNo)) {
                log.warn("Skip execution because fileNo is empty.");
            } else {
                cache.evict("fileNoToFileUrl" + fileNo);
            }
        }
    }
}
