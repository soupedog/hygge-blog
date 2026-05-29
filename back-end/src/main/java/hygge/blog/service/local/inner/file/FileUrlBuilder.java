package hygge.blog.service.local.inner.file;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Xavier
 * @date 2026/5/26
 */
@Component
public class FileUrlBuilder {
    public final String apiUrlPrefix;
    public final String nginxUrlPrefix;

    public FileUrlBuilder(@Value("${hyyge.blog.file.expose.api.prefix}") String apiUrlPrefix,
                          @Value("${hyyge.blog.file.expose.nginx.prefix}") String nginxUrlPrefix) {
        this.apiUrlPrefix = autoFillForwardSlashToEnd(apiUrlPrefix);
        this.nginxUrlPrefix = autoRemoveLastForwardSlash(nginxUrlPrefix);
    }

    public String getFileNginxLinkByRelativePath(String relativePath) {
        return nginxUrlPrefix + relativePath;
    }

    public String getFileApiLinkByFileNo(String fileNo) {
        return apiUrlPrefix + fileNo;
    }

    public String autoFillForwardSlashToEnd(String rawPrefix) {
        // 自动补齐结尾正斜杠
        return rawPrefix.endsWith("/")
                ? rawPrefix
                : rawPrefix + "/";
    }

    public String autoRemoveLastForwardSlash(String rawPrefix) {
        // 自动去除结尾正斜杠
        return rawPrefix.endsWith("/")
                ? rawPrefix.substring(0, rawPrefix.length() - 1)
                : rawPrefix;
    }
}
