package hygge.blog.service.local.inner.file;

import hygge.commons.exception.InternalRuntimeException;
import hygge.util.UtilCreator;
import hygge.util.definition.RandomHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 将当前服务 API 暴露的文件链接解析成 fileNo
 *
 * @author Xavier
 * @date 2025/11/10
 */
@Slf4j
@Component
public class ApiFileNoLinkPicker implements HyggeFileNoLinkPicker {
    private final Pattern pattern;

    public ApiFileNoLinkPicker(@Value("${hyyge.blog.file.expose.api.prefix}") String nginxUrlPrefix) {
        String regex = String.format("^%s([0-9a-fA-F]{32})(?:[?#].*)?", nginxUrlPrefix);
        this.pattern = Pattern.compile(regex);

        String exampleLink = nginxUrlPrefix + UtilCreator.INSTANCE.getDefaultInstance(RandomHelper.class).getUniversallyUniqueIdentifier(true);
        validate(exampleLink);
    }

    @Override
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

    @Override
    public void validate(String targetLink) {
        String fileNo = tryToGetFileNo(targetLink);

        if (fileNo != null) {
            log.info("{} init success! exampleLink:{} → result:{}", this.getClass().getSimpleName(), targetLink, fileNo);
        } else {
            throw new InternalRuntimeException(this.getClass().getSimpleName() + " init failed.");
        }
    }
}
