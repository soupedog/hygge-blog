package hygge.blog.job.key;

import hygge.job.HyggeJobContextKey;

/**
 * @author Xavier
 * @date 2026/7/2
 */
public enum RefreshArticleJobKey implements HyggeJobContextKey {
    ALL_CATEGORY_MAP,
    ;

    @Override
    public Enum<?> getKey() {
        return this;
    }
}
