package hygge.blog.service.local.inner.markdown.image;

/**
 * @author Xavier
 * @date 2026/5/28
 */
public interface ResourceReplacer<T> {
    ReplaceCheckResult checkResource(String resource);

    ReplaceCheckResult checkResource(String resource, T extendObject);
}
