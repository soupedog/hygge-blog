package hygge.blog.service.local;

import hygge.blog.domain.local.bo.CacheObjectContainer;
import hygge.util.template.HyggeJsonUtilContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Xavier
 * @date 2026/5/28
 */
@Service
public class CacheServiceWithBusinessLogicImpl extends HyggeJsonUtilContainer {
    private final CacheServiceImpl cacheService;
    private final FileServiceImpl fileService;

    @Autowired
    public CacheServiceWithBusinessLogicImpl(CacheServiceImpl cacheService, FileServiceImpl fileService) {
        this.cacheService = cacheService;
        this.fileService = fileService;
    }

    /**
     * 这个方法返回的 URL 会自动补充文件授权秘钥，保障所暴露的图片至少能被访问一次
     */
    public String smartGetAccessUrl(String fileNo) {
        if (parameterHelper.isEmpty(fileNo)) {
            return null;
        }

        CacheObjectContainer.FileAccessUrl resultTemp = cacheService.fileNoToFileUrl(fileNo);

        String result = null;

        if (resultTemp != null) {
            result = resultTemp.getSrc();

            // 非公开类型自动授权
            if (resultTemp.isApiLink() && !resultTemp.isPublic()) {
                result = result + "?fileKey=" + fileService.generateOneTimeFileKey(fileNo);
            }
        }
        return result;
    }

    public String getAccessUrlIfPublic(String fileNo) {
        CacheObjectContainer.FileAccessUrl resultTemp = cacheService.fileNoToFileUrl(fileNo);
        String result = null;

        if (resultTemp != null && resultTemp.isPublic()) {
            result = resultTemp.getSrc();
        }

        return result;
    }
}
