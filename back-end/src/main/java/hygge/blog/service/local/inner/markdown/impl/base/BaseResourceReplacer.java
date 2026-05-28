package hygge.blog.service.local.inner.markdown.impl.base;

import hygge.blog.service.local.inner.markdown.ResourceReplacer;
import hygge.blog.service.local.inner.markdown.ReplaceCheckResult;

/**
 * @author Xavier
 * @date 2026/5/28
 */
public abstract class BaseResourceReplacer<T> implements ResourceReplacer<T> {
    public abstract boolean isExtendObjectEnable();

    public abstract T getExtendObject();

    public ReplaceCheckResult smartCheckResource(String resource) {
        if (isExtendObjectEnable()) {
            return checkResource(resource, getExtendObject());
        } else {
            return checkResource(resource);
        }
    }
}
