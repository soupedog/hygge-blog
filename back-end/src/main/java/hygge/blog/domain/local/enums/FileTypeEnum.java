package hygge.blog.domain.local.enums;

import java.io.File;
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
    CORE(File.separator + "core" + File.separator),
    /**
     * 句子收藏用文件
     */
    QUOTE(File.separator + "quote" + File.separator),
    /**
     * 博文用封面
     */
    ARTICLE_COVER(File.separator + "article" + File.separator + "cover" + File.separator),
    /**
     * 博文用文件
     */
    ARTICLE(File.separator + "article" + File.separator),
    /**
     * 文章背景音乐
     */
    BGM(File.separator + "bgm" + File.separator),
    /**
     * 杂项文件
     */
    OTHERS(File.separator + "others" + File.separator),
    ;
    private final String path;

    FileTypeEnum(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
    /**
     * 标准化 URL 路径，跨平台统一为正斜杠，首尾带 /
     */
    public String getUrlPath() {
        String normalized = path.replace("\\", "/");
        if (!normalized.startsWith("/")) {
            normalized = "/" + normalized;
        }
        if (!normalized.endsWith("/")) {
            normalized = normalized + "/";
        }
        return normalized;
    }

    /**
     * 按路径深度倒序，长的优先匹配
     */
    public static List<FileTypeEnum> getAllByPathDepthDesc() {
        return Arrays.stream(values())
                .sorted(Comparator
                        .comparingInt((FileTypeEnum e) -> e.getUrlPath().length())
                        .reversed())
                .collect(Collectors.toList());
    }

    /**
     * 根据标准化路径获取对应枚举
     */
    public static FileTypeEnum fromUrlPath(String urlPath) {
        for (FileTypeEnum e : values()) {
            if (e.getUrlPath().equals(urlPath)) {
                return e;
            }
        }
        return null;
    }
}
