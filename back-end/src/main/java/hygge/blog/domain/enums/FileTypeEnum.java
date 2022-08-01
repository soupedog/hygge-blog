package hygge.blog.domain.enums;

import java.io.File;

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
    ;
    private final String path;

    FileTypeEnum(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
