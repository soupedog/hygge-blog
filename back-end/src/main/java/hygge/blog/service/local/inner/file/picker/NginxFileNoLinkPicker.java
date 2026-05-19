package hygge.blog.service.local.inner.file.picker;

import hygge.blog.domain.local.enums.FileTypeEnum;
import hygge.blog.domain.local.po.view.FileInfoView;
import hygge.blog.service.local.FileServiceImpl;
import hygge.blog.service.local.inner.file.HyggeFileNoLinkPicker;
import hygge.commons.exception.InternalRuntimeException;
import hygge.util.UtilCreator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Xavier
 * @date 2026/5/19
 */
@Slf4j
@Component
public class NginxFileNoLinkPicker implements HyggeFileNoLinkPicker {
    private final Pattern pattern;
    public final String nginxUrlPrefix;
    private final FileServiceImpl fileService;

    private boolean validateMode = true;
    private Map<String, FileInfoView> forValidateMap;

    public NginxFileNoLinkPicker(@Value("${hyyge.blog.file.expose.nginx.prefix}") String nginxUrlPrefix, FileServiceImpl fileService) {
        // 自动去除结尾反斜杠
        String normalizedPrefix = nginxUrlPrefix.endsWith("/")
                ? nginxUrlPrefix.substring(0, nginxUrlPrefix.length() - 1)
                : nginxUrlPrefix;

        String regex = buildRegex(normalizedPrefix);
        this.pattern = Pattern.compile(regex);
        this.nginxUrlPrefix = normalizedPrefix;
        this.fileService = fileService;

        validate();
        cleanForValidate();
    }

    @Override
    public String tryToGetFileNo(String targetLink) {
        if (targetLink == null || targetLink.isEmpty()) {
            return null;
        }

        Matcher matcher = pattern.matcher(targetLink);
        if (matcher.find()) {
            String categoryPath = matcher.group(1);
            String filename = matcher.group("filename");
            String extension = matcher.group("ext");

            // 反查枚举
            FileTypeEnum fileType = FileTypeEnum.fromUrlPath(categoryPath);

            if (fileType != null
                    && filename != null
                    && extension != null) {
                FileInfoView fileInfoView;

                // 验证模式不访问数据库，粗略检测正则
                if (validateMode) {
                    fileInfoView = forValidateMap.get(createValidateKey(fileType, filename, extension));
                } else {
                    fileInfoView = fileService.findFileInfoView(fileType, filename, extension);
                }

                if (fileInfoView != null) {
                    return fileInfoView.getFileNo();
                }
            }
        }

        return null;
    }


    @Override
    public void validate() {
        forValidateMap = new HashMap<>();

        List<String> successLinks = new ArrayList<>();

        FileTypeEnum[] values = FileTypeEnum.values();

        for (int i = 0; i < values.length; i++) {
            FileTypeEnum fileTypeEnum = values[i];
            FileInfoView fileInfoView = FileInfoView.builder().fileNo(fileTypeEnum.name()).build();

            String name = "图片" + i;
            String extension = "jpg";

            String testKey = createValidateKey(fileTypeEnum, name, extension);
            forValidateMap.put(testKey, fileInfoView);

            String link = nginxUrlPrefix + fileTypeEnum.getPath() + name + "." + extension;
            if (ThreadLocalRandom.current().nextBoolean()) {
                link = link + "?fileKey=XXXXXX";
            }

            if (!fileTypeEnum.name().equals(tryToGetFileNo(link))) {
                throw new InternalRuntimeException(this.getClass().getSimpleName() + " init failed(" + link + ").");
            }
            successLinks.add(link);
        }

        String exampleLinks = UtilCreator.INSTANCE.getDefaultJsonHelperInstance(false).formatAsString(successLinks);

        this.validateMode = false;
        log.info("{} init success! exampleLinks:{}", this.getClass().getSimpleName(), exampleLinks);
    }

    private void cleanForValidate() {
        this.forValidateMap = null;
    }

    private String createValidateKey(FileTypeEnum fileType, String name, String extension) {
        return fileType.name() + "-" + name + "-" + extension;
    }

    private static String buildRegex(String nginxUrlPrefix) {
        String escapedPrefix = Pattern.quote(nginxUrlPrefix);

        // 每个枚举路径放在独立的捕获组中，用 | 连接
        // 如 (/core/)|(/article/cover/)|...
        String pathRegex = FileTypeEnum.getAllByPathDepthDesc().stream()
                .map(e -> "(" + Pattern.quote(e.getPath()) + ")")
                .collect(Collectors.joining("|"));

        // 路径捕获组就是 group(1)，哪个分支匹配到，group(1) 就是那个值
        return "^" + escapedPrefix + "(" + pathRegex + ")(?<filename>[^/]+?)\\.(?<ext>[^./?]+)(\\?.*)?$";
    }
}
