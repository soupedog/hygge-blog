package hygge.blog.service.local.inner.markdown;

import com.vladsch.flexmark.ast.Image;
import com.vladsch.flexmark.util.ast.Visitor;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import hygge.blog.service.local.inner.file.CacheFileKeyKeeper;
import hygge.blog.service.local.inner.file.picker.ApiFileNoLinkPicker;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/**
 * 给 markdown 文档里通过 API 方式对外暴露的图片出初始化可访问一次的授权
 *
 * @author Xavier
 * @date 2025/11/10
 */
@Component
public class ImageResourceOneTimeAuthorizationVisitor implements Visitor<Image> {
    private final ApiFileNoLinkPicker apiFileNoLinkPicker;
    private final CacheFileKeyKeeper fileKeyKeeper;

    public ImageResourceOneTimeAuthorizationVisitor(ApiFileNoLinkPicker apiFileNoLinkPicker, CacheFileKeyKeeper fileKeyKeeper) {
        this.apiFileNoLinkPicker = apiFileNoLinkPicker;
        this.fileKeyKeeper = fileKeyKeeper;
    }

    @Override
    public void visit(@NotNull Image image) {
        String rawUrl = image.getUrl().toString();

        String fileNo = apiFileNoLinkPicker.tryToGetFileNo(rawUrl);

        // 匹配上格式，能取得 fileNo 就默认是当前系统通过 API 暴露的图片，准备进行一次性授权
        if (fileNo != null) {
            String fileKey = fileKeyKeeper.createFileKey(fileNo);
            String newUrl = apiFileNoLinkPicker.apiUrlPrefix + fileNo + "?fileKey=" + fileKey;
            // 本地调试可用
            // String newUrl = "http://localhost:8080/blog-service/api/main/file/static/" + fileNo + "?fileKey=" + fileKey;
            BasedSequence basedSequence = BasedSequence.of(newUrl);
            image.setUrl(basedSequence);
            image.setPageRef(basedSequence);
        }
    }
}
