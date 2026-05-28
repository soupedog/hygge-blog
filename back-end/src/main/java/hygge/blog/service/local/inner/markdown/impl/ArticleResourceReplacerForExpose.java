package hygge.blog.service.local.inner.markdown.impl;

import hygge.blog.domain.local.po.Article;
import hygge.blog.domain.local.po.view.FileInfoView;
import hygge.blog.service.local.FileServiceImpl;
import hygge.blog.service.local.inner.markdown.ReplaceCheckResult;
import hygge.blog.service.local.inner.markdown.impl.base.BaseResourceReplacer;
import hygge.blog.service.local.normal.PermissionServiceImpl;
import hygge.commons.exception.InternalRuntimeException;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * @author Xavier
 * @date 2026/5/28
 */
public class ArticleResourceReplacerForExpose extends BaseResourceReplacer<Article> {
    private static final Pattern UUID_32_PATTERN = Pattern.compile("^[0-9a-fA-F]{32}$");

    private final FileServiceImpl fileService;

    public ArticleResourceReplacerForExpose(FileServiceImpl fileService) {
        this.fileService = fileService;
    }

    @Override
    public boolean isExtendObjectEnable() {
        return false;
    }

    @Override
    public Article getExtendObject() {
        throw new InternalRuntimeException("Reached unreachable code.");
    }

    @Override
    public ReplaceCheckResult checkResource(String resource) {
        boolean needReplace = false;
        String newResource = null;

        if (resource != null) {
            if (UUID_32_PATTERN.matcher(resource).matches()) {
                Optional<FileInfoView> fileInfoViewTemp = fileService.findFileViewFromDB(resource);
                if (fileInfoViewTemp.isPresent()) {
                    FileInfoView fileInfoView = fileInfoViewTemp.get();
                    newResource = fileService.getFileAccessUrl(fileInfoView);
                    // 非公开文件则直接发放一次性文件授权
                    if (!fileInfoView.getPermissionId().equals(PermissionServiceImpl._PUBLIC.getPermissionId())) {
                        newResource = newResource + "?fileKey=" + fileService.generateOneTimeFileKey(fileInfoView.getFileNo());
                    }
                    needReplace = true;
                }
            }
        }

        return ReplaceCheckResult.builder()
                .needReplace(needReplace)
                .newResource(newResource)
                .build();
    }

    @Override
    public ReplaceCheckResult checkResource(String resource, Article extendObject) {
        throw new InternalRuntimeException("Reached unreachable code.");
    }
}
