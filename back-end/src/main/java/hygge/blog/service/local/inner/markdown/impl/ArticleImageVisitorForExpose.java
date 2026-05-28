package hygge.blog.service.local.inner.markdown.impl;

import com.vladsch.flexmark.ast.Image;
import com.vladsch.flexmark.util.ast.Visitor;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import hygge.blog.service.local.CacheServiceWithBusinessLogicImpl;
import hygge.blog.service.local.inner.markdown.ReplaceCheckResult;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

/**
 * @author Xavier
 * @date 2026/5/28
 */
@Slf4j
public class ArticleImageVisitorForExpose implements Visitor<Image> {
    private final ArticleResourceReplacerForExpose resourceReplacer;

    public ArticleImageVisitorForExpose(CacheServiceWithBusinessLogicImpl cacheServiceWithBusinessLogic) {
        this.resourceReplacer = new ArticleResourceReplacerForExpose(cacheServiceWithBusinessLogic);
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
            log.debug("Image({}) replace to {{}}", rawUrl, newUrl);
        }
    }
}
