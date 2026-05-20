package hygge.blog.service.local.inner.markdown;

import com.vladsch.flexmark.ast.Image;
import com.vladsch.flexmark.util.ast.Visitor;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import hygge.blog.service.local.inner.file.picker.ApiFileNoLinkPicker;
import hygge.blog.service.local.inner.file.picker.NginxFileNoLinkPicker;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

/**
 * 给 markdown 文档里旧自有的图片链接(包括 API 和 Nginx)替换为最新的 API 文件资源链接
 *
 * @author Xavier
 * @date 2026/5/19
 */
@Slf4j
public class ImageResourceOldToApiVisitor implements Visitor<Image> {
    private final ApiFileNoLinkPicker apiFileNoLinkPicker;
    private final ApiFileNoLinkPicker old_apiFileNoLinkPicker;
    private final NginxFileNoLinkPicker old_nginxFileNoLinkPicker;

    public ImageResourceOldToApiVisitor(ApiFileNoLinkPicker apiFileNoLinkPicker, ApiFileNoLinkPicker old_apiFileNoLinkPicker, NginxFileNoLinkPicker old_nginxFileNoLinkPicker) {
        this.apiFileNoLinkPicker = apiFileNoLinkPicker;
        this.old_apiFileNoLinkPicker = old_apiFileNoLinkPicker;
        this.old_nginxFileNoLinkPicker = old_nginxFileNoLinkPicker;
    }

    @Override
    public void visit(@NotNull Image image) {
        String rawUrl = image.getUrl().toString();

        String fileNo = old_apiFileNoLinkPicker.tryToGetFileNo(rawUrl);

        // 旧版本 api 类型的图片未找到则尝试旧版本 nginx 类型
        if (fileNo == null) {
            fileNo = old_nginxFileNoLinkPicker.tryToGetFileNo(rawUrl);
        }

        // 匹配上格式，就认为是可替换的目标资源
        if (fileNo != null) {
            String newUrl = apiFileNoLinkPicker.apiUrlPrefix + fileNo;
            BasedSequence basedSequence = BasedSequence.of(newUrl);
            image.setUrl(basedSequence);
            image.setPageRef(basedSequence);
            log.info("replace Image {} to {}", rawUrl, newUrl);
        }
    }
}
