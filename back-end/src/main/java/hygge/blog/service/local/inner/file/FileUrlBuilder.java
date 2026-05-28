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
    public final String apiUrlPrefix_old;
    public final String nginxUrlPrefix;
    public final String nginxUrlPrefix_old;

    public FileUrlBuilder(@Value("${hyyge.blog.file.expose.api.prefix}") String apiUrlPrefix,
                          @Value("${hyyge.blog.file.expose.old.api.prefix:old.com/}") String apiUrlPrefix_old,
                          @Value("${hyyge.blog.file.expose.nginx.prefix}") String nginxUrlPrefix,
                          @Value("${hyyge.blog.file.expose.old.nginx.prefix:old.com/static/}") String nginxUrlPrefix_old) {
        this.apiUrlPrefix = autoFillForwardSlashToEnd(apiUrlPrefix);
        this.apiUrlPrefix_old = autoFillForwardSlashToEnd(apiUrlPrefix_old);
        this.nginxUrlPrefix = autoRemoveLastForwardSlash(nginxUrlPrefix);
        this.nginxUrlPrefix_old = autoRemoveLastForwardSlash(nginxUrlPrefix_old);
    }

    public String getFileCacheLinkByRelativePath(String relativePath) {
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
