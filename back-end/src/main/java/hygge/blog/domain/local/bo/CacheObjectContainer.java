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
    public static final String NAME_CATEGORY_TREE = "categoryTreeInfoCache";
    public static final String NAME_USERID_UID_MAPPING = "userIdToUidMappingCache";
    public static final String NAME_FILE_NO_URL_MAPPING = "fileNoToFileUrlMappingCache";

    @Getter
    public enum CacheTypeEnum {
        CATEGORY_TREE(0, NAME_CATEGORY_TREE),
        USERID_UID_MAPPING(1, NAME_USERID_UID_MAPPING),
        FILE_NO_URL_MAPPING(2, NAME_FILE_NO_URL_MAPPING),
        ;

        CacheTypeEnum(Integer index, String value) {
            this.index = index;
            this.value = value;
        }

        /**
         * 序号
         */
        private Integer index;
        /**
         * 枚举值
         */
        private String value;
    }

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
