package hygge.blog.domain.local.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Xavier
 * @date 2026/5/28
 */
public class CacheObjectContainer {

    @Getter
    @Setter
    @Builder
    @Generated
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FileAccessUrl {
        private String src;
        private boolean isPublic;
        private boolean isApiLink;
    }
}
