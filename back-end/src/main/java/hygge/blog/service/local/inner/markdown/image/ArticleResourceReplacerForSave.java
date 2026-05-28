package hygge.blog.service.local.inner.markdown.image;

import hygge.blog.domain.local.po.Article;
import hygge.blog.service.local.inner.file.picker.ApiFileNoLinkPicker;
import hygge.blog.service.local.inner.file.picker.NginxFileNoLinkPicker;
import hygge.commons.exception.InternalRuntimeException;

/**
 * @author Xavier
 * @date 2026/5/28
 */
public class ArticleResourceReplacerForSave extends BaseResourceReplacer<Article> {
    private final Article article;
    private final ApiFileNoLinkPicker apiFileNoLinkPicker;
    private final NginxFileNoLinkPicker nginxFileNoLinkPicker;

    public ArticleResourceReplacerForSave(Article article, ApiFileNoLinkPicker apiFileNoLinkPicker, NginxFileNoLinkPicker nginxFileNoLinkPicker) {
        this.article = article;
        this.apiFileNoLinkPicker = apiFileNoLinkPicker;
        this.nginxFileNoLinkPicker = nginxFileNoLinkPicker;
    }

    @Override
    public boolean isExtendObjectEnable() {
        return true;
    }

    @Override
    public Article getExtendObject() {
        return article;
    }

    @Override
    public ReplaceCheckResult checkResource(String resource) {
        throw new InternalRuntimeException("Reached unreachable code.");
    }

    @Override
    public ReplaceCheckResult checkResource(String resource, Article extendObject) {
        boolean needReplace = false;
        String newResource = null;

        String fileNo = apiFileNoLinkPicker.tryToGetFileNo(resource);

        if (fileNo == null) {
            fileNo = nginxFileNoLinkPicker.tryToGetFileNo(resource);
        }

        if (fileNo != null) {
            needReplace = true;
            newResource = fileNo;
        }

        return ReplaceCheckResult.builder()
                .needReplace(needReplace)
                .newResource(newResource)
                .build();
    }
}
