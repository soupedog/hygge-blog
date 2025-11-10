package hygge.blog.service.local.inner.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Xavier
 * @date 2025/11/10
 */
@Slf4j
@Component
public class MyFileLinkPicker {
    private final Pattern pattern;

    public MyFileLinkPicker(@Value("^${hyyge.blog.file.link.prefix}/([0-9a-fA-F]{32})(?:[?#].*)?") String regex) {
        this.pattern = Pattern.compile(regex);
        log.info("pattern init with 【{}】 success.", regex);
    }

    public String tryToGetFileNo(String targetLink) {
        if (targetLink == null || targetLink.isEmpty()) {
            return null;
        }

        Matcher matcher = pattern.matcher(targetLink);
        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }
}
