package hygge.blog.service.local.inner.markdown.image;

import com.vladsch.flexmark.ast.Image;
import com.vladsch.flexmark.util.ast.Visitor;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import hygge.blog.domain.local.po.Article;
import hygge.blog.service.local.FileNoPickerServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

/**
 * @author Xavier
 * @date 2026/5/28
 */
@Slf4j
public class ArticleImageVisitorForSave implements Visitor<Image> {
    private final ArticleResourceReplacerForSave resourceReplacer;
    private final Article article;

    public ArticleImageVisitorForSave(Article article, FileNoPickerServiceImpl fileNoPickerService) {
        this.resourceReplacer = new ArticleResourceReplacerForSave(article, fileNoPickerService.apiFileNoLinkPicker, fileNoPickerService.nginxFileNoLinkPicker);
        this.article = article;
    }

    @Override
    public void visit(@NotNull Image node) {
        String rawUrl = node.getUrl().toString();

        ReplaceCheckResult checkResult = resourceReplacer.smartCheckResource(rawUrl);

        if (checkResult.isNeedReplace()) {
            String newUrl = checkResult.getNewResource();
            BasedSequence basedSequence = BasedSequence.of(checkResult.getNewResource());
            node.setUrl(basedSequence);
            node.setPageRef(basedSequence);
            log.debug("Article({}) Image({}) replace to {{}}", article.getTitle(), rawUrl, newUrl);
        }
    }
}
