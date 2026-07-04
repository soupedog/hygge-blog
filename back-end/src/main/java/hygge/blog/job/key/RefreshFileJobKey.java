package hygge.blog.job.key;

import hygge.job.HyggeJobContextKey;

/**
 * @author Xavier
 * @date 2026/7/4
 */
public enum RefreshFileJobKey implements HyggeJobContextKey {
    IS_UPDATE_MODE,
    ;

    @Override
    public Enum<?> getKey() {
        return this;
    }
}
