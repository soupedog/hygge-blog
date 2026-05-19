package hygge.blog.service.local.inner.file;

/**
 * @author Xavier
 * @date 2026/5/19
 */
public interface HyggeFileNoLinkPicker {
    /**
     * 尝试从 targetLink 中提取 fileNo，不存在则返回 null
     *
     */
    String tryToGetFileNo(String targetLink);

    /**
     * 用于启动后自检，提取规则是否有效
     */
    void validate(String targetLink);
}
