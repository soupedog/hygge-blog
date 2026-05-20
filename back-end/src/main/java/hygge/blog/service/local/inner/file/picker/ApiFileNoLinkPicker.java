package hygge.blog.service.local.inner.file.picker;

import hygge.blog.service.local.inner.file.HyggeFileNoLinkPicker;
import hygge.commons.exception.InternalRuntimeException;
import hygge.util.UtilCreator;
import hygge.util.definition.RandomHelper;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 将当前服务 API 暴露的文件链接解析成 fileNo
 *
 * @author Xavier
 * @date 2025/11/10
 */
@Slf4j
public class ApiFileNoLinkPicker implements HyggeFileNoLinkPicker {
    private final Pattern pattern;
    public final String apiUrlPrefix;

    public ApiFileNoLinkPicker(String apiUrlPrefix) {
        // 自动补齐结尾反斜杠
        String normalizedPrefix = apiUrlPrefix.endsWith("/")
                ? apiUrlPrefix
                : apiUrlPrefix + "/";

        String regex = String.format("^%s([0-9a-fA-F]{32})(?:[?#].*)?", normalizedPrefix);

        this.pattern = Pattern.compile(regex);
        this.apiUrlPrefix = normalizedPrefix;

        validate();
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
    public void validate() {
        String exampleLink = apiUrlPrefix + UtilCreator.INSTANCE.getDefaultInstance(RandomHelper.class).getUniversallyUniqueIdentifier(true);
        String fileNo = tryToGetFileNo(exampleLink);

        if (fileNo != null) {
            log.info("{} init success! exampleLink:{} → result:{}", this.getClass().getSimpleName(), exampleLink, fileNo);
        } else {
            throw new InternalRuntimeException(this.getClass().getSimpleName() + " init failed.");
        }
    }
}
