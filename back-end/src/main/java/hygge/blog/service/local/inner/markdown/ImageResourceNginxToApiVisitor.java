package hygge.blog.service.local.inner.markdown;

import com.vladsch.flexmark.ast.Image;
import com.vladsch.flexmark.util.ast.Visitor;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import hygge.blog.service.local.inner.file.picker.ApiFileNoLinkPicker;
import hygge.blog.service.local.inner.file.picker.NginxFileNoLinkPicker;
import org.jetbrains.annotations.NotNull;

/**
 * 给 markdown 文档里通过 API 方式对外暴露的图片替换为 Nginx 缓存资源(如果存在的话)
 *
 * @author Xavier
 * @date 2026/5/19
 */
public class ImageResourceNginxToApiVisitor implements Visitor<Image> {
    private final ApiFileNoLinkPicker apiFileNoLinkPicker;
    private final NginxFileNoLinkPicker nginxFileNoLinkPicker;

    public ImageResourceNginxToApiVisitor(ApiFileNoLinkPicker apiFileNoLinkPicker, NginxFileNoLinkPicker nginxFileNoLinkPicker) {
        this.apiFileNoLinkPicker = apiFileNoLinkPicker;
        this.nginxFileNoLinkPicker = nginxFileNoLinkPicker;
    }

    @Override
    public void visit(@NotNull Image image) {
        String rawUrl = image.getUrl().toString();

        String fileNo = nginxFileNoLinkPicker.tryToGetFileNo(rawUrl);

        // 匹配上格式，就认为是可替换的目标资源
        if (fileNo != null) {
            String newUrl = apiFileNoLinkPicker.apiUrlPrefix + fileNo;
            BasedSequence basedSequence = BasedSequence.of(newUrl);
            image.setUrl(basedSequence);
            image.setPageRef(basedSequence);
        }
    }
}
