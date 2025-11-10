package hygge.blog.service.local.inner.file;

/**
 * 文件秘钥托管器
 *
 * @author Xavier
 * @date 2025/11/10
 */
public interface FileKeyKeeper {

    /**
     * 文件秘钥文本生成器，不包含业务，单纯生成字符串机制
     */
    String genderKey(String fileNo);

    /**
     * 为特定文件生成文件秘钥
     *
     * @return 文件秘钥
     */
    String createFileKey(String fileNo);

    /**
     * 核销特定文件的秘钥是否成功
     *
     * @return true 则为核销成功，否则核销失败
     */
    boolean writeOffFileKey(String fileNo, String fileKey);
}
