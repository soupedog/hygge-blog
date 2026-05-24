package hygge.blog.domain.local.enums;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Xavier
 * @date 2022/8/1
 */
public enum FileTypeEnum {
    /**
     * 系统默认必须的一些图片
     */
    CORE("/core/"),
    /**
     * 句子收藏用文件
     */
    QUOTE("/quote/"),
    /**
     * 博文用封面
     */
    ARTICLE_COVER("/article/cover/"),
    /**
     * 博文用文件
     */
    ARTICLE("/article/"),
    /**
     * 文章背景音乐
     */
    BGM("/bgm/"),
    /**
     * 杂项文件
     */
    OTHERS("/others/"),
    ;
    private final String path;

    FileTypeEnum(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    /**
     * 按路径深度倒序，长的优先匹配
     */
    public static List<FileTypeEnum> getAllByPathDepthDesc() {
        return Arrays.stream(values())
                .sorted(Comparator
                        .comparingInt((FileTypeEnum e) -> e.getPath().length())
                        .reversed())
                .collect(Collectors.toList());
    }

    /**
     * 根据标准化路径获取对应枚举
     */
    public static FileTypeEnum fromUrlPath(String urlPath) {
        for (FileTypeEnum e : values()) {
            if (e.getPath().equals(urlPath)) {
                return e;
            }
        }
        return null;
    }
}
