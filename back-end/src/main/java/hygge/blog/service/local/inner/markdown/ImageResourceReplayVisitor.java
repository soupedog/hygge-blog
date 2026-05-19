package hygge.blog.service.local.inner.markdown;

import com.vladsch.flexmark.ast.Image;
import com.vladsch.flexmark.util.ast.Visitor;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import hygge.blog.service.local.inner.file.CacheFileKeyKeeper;
import hygge.blog.service.local.inner.file.ApiFileNoLinkPicker;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Xavier
 * @date 2025/11/10
 */

@Component
public class ImageResourceReplayVisitor implements Visitor<Image> {
    @Value("${hyyge.blog.file.expose.api.prefix}")
    private String linkPrefix;
    private final ApiFileNoLinkPicker myNginxFileLinkPicker;
    private final CacheFileKeyKeeper fileKeyKeeper;

    public ImageResourceReplayVisitor(ApiFileNoLinkPicker myNginxFileLinkPicker, CacheFileKeyKeeper fileKeyKeeper) {
        this.myNginxFileLinkPicker = myNginxFileLinkPicker;
        this.fileKeyKeeper = fileKeyKeeper;
    }

    @Override
    public void visit(@NotNull Image image) {
        String rawUrl = image.getUrl().toString();

        String fileNo = myNginxFileLinkPicker.tryToGetFileNo(rawUrl);

        if (fileNo != null) {
            // 是需要被保护的文件资源
            String fileKey = fileKeyKeeper.createFileKey(fileNo);
            String newUrl = linkPrefix + fileNo + "?fileKey=" + fileKey;
            // 本地调试可用
            // String newUrl = "http://localhost:8080/blog-service/api/main/file/static/" + fileNo + "?fileKey=" + fileKey;
            BasedSequence basedSequence = BasedSequence.of(newUrl);
            image.setUrl(basedSequence);
            image.setPageRef(basedSequence);
        }
    }
}
