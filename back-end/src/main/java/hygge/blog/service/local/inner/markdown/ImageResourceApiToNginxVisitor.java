package hygge.blog.service.local.inner.markdown;

import com.vladsch.flexmark.ast.Image;
import com.vladsch.flexmark.util.ast.Visitor;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import hygge.blog.domain.local.po.view.FileInfoView;
import hygge.blog.service.local.FileServiceImpl;
import hygge.blog.service.local.inner.file.picker.ApiFileNoLinkPicker;
import hygge.blog.service.local.inner.file.picker.NginxFileNoLinkPicker;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/**
 * 给 markdown 文档里通过 API 方式对外暴露的图片替换为 Nginx 缓存资源(如果存在的话)
 *
 * @author Xavier
 * @date 2026/5/19
 */
@Component
public class ImageResourceApiToNginxVisitor implements Visitor<Image> {
    private final ApiFileNoLinkPicker apiFileNoLinkPicker;
    private final NginxFileNoLinkPicker nginxFileNoLinkPicker;
    private final FileServiceImpl fileService;

    public ImageResourceApiToNginxVisitor(ApiFileNoLinkPicker apiFileNoLinkPicker, NginxFileNoLinkPicker nginxFileNoLinkPicker, FileServiceImpl fileService) {
        this.apiFileNoLinkPicker = apiFileNoLinkPicker;
        this.nginxFileNoLinkPicker = nginxFileNoLinkPicker;
        this.fileService = fileService;
    }

    @Override
    public void visit(@NotNull Image image) {
        String rawUrl = image.getUrl().toString();

        String fileNo = apiFileNoLinkPicker.tryToGetFileNo(rawUrl);

        // 匹配上格式，就认为是可替换的目标资源
        if (fileNo != null) {
            FileInfoView fileInfoView = fileService.findFileViewFromDB(fileNo).orElseGet(null);

            if (fileInfoView != null) {
                String newUrl = nginxFileNoLinkPicker.nginxUrlPrefix + fileInfoView.returnRelativePath();
                BasedSequence basedSequence = BasedSequence.of(newUrl);
                image.setUrl(basedSequence);
                image.setPageRef(basedSequence);
            }
        }
    }
}
