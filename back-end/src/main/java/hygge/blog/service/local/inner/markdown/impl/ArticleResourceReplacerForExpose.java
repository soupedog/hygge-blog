package hygge.blog.service.local.inner.markdown.impl;

import hygge.blog.domain.local.po.Article;
import hygge.blog.service.local.CacheServiceWithBusinessLogicImpl;
import hygge.blog.service.local.inner.markdown.ReplaceCheckResult;
import hygge.blog.service.local.inner.markdown.impl.base.BaseResourceReplacer;
import hygge.commons.exception.InternalRuntimeException;

import java.util.regex.Pattern;

/**
 * @author Xavier
 * @date 2026/5/28
 */
public class ArticleResourceReplacerForExpose extends BaseResourceReplacer<Article> {
    private static final Pattern UUID_32_PATTERN = Pattern.compile("^[0-9a-fA-F]{32}$");

    private final CacheServiceWithBusinessLogicImpl cacheServiceWithBusinessLogic;

    public ArticleResourceReplacerForExpose(CacheServiceWithBusinessLogicImpl cacheServiceWithBusinessLogic) {
        this.cacheServiceWithBusinessLogic = cacheServiceWithBusinessLogic;
    }

    @Override
    public boolean isExtendObjectEnable() {
        return false;
    }

    @Override
    public Article getExtendObject() {
        throw new InternalRuntimeException("Reached unreachable code.");
    }

    @Override
    public ReplaceCheckResult checkResource(String resource) {
        boolean needReplace = false;
        String newResource = null;

        if (resource != null) {
            if (UUID_32_PATTERN.matcher(resource).matches()) {
                newResource = cacheServiceWithBusinessLogic.smartGetAccessUrl(resource);

                if (newResource != null) {
                    needReplace = true;
                }
            }
        }

        return ReplaceCheckResult.builder()
                .needReplace(needReplace)
                .newResource(newResource)
                .build();
    }

    @Override
    public ReplaceCheckResult checkResource(String resource, Article extendObject) {
        throw new InternalRuntimeException("Reached unreachable code.");
    }
}
