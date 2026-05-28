package hygge.blog.service.local.inner.markdown.image;

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
