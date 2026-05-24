package hygge.blog.service.local.inner.markdown;

import com.vladsch.flexmark.ast.Image;
import com.vladsch.flexmark.util.ast.Visitor;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import hygge.blog.domain.local.enums.AccessConditionTypeEnum;
import hygge.blog.domain.local.po.Category;
import hygge.blog.domain.local.po.view.FileInfoView;
import hygge.blog.service.local.inner.file.picker.ApiFileNoLinkPicker;
import hygge.blog.service.local.inner.file.picker.NginxFileNoLinkPicker;
import org.jetbrains.annotations.NotNull;

/**
 * 给 markdown 文档里通过 API 方式对外暴露的图片替换为 Nginx 缓存资源(如果存在的话)
 *
 * @author Xavier
 * @date 2026/5/19
 */
public class ImageResourceApiToNginxVisitor implements Visitor<Image> {
    private final ApiFileNoLinkPicker apiFileNoLinkPicker;
    private final NginxFileNoLinkPicker nginxFileNoLinkPicker;

    public ImageResourceApiToNginxVisitor(ApiFileNoLinkPicker apiFileNoLinkPicker, NginxFileNoLinkPicker nginxFileNoLinkPicker) {
        this.apiFileNoLinkPicker = apiFileNoLinkPicker;
        this.nginxFileNoLinkPicker = nginxFileNoLinkPicker;
    }

    @Override
    public void visit(@NotNull Image image) {
        String rawUrl = image.getUrl().toString();

        String fileNo = apiFileNoLinkPicker.tryToGetFileNo(rawUrl);

        if (fileNo != null) {
            // TODO 权限系统改造后这里也得相应处理
            FileInfoView fileInfoView = nginxFileNoLinkPicker.getFileService().findFileViewFromDB(fileNo).orElseGet(null);

            // 匹配上格式，就认为是可替换的目标资源
            if (fileInfoView != null) {
                // 是否可以对外暴露
                boolean canExpose = true;

                if (fileInfoView.getCid() != null) {
                    Category category = nginxFileNoLinkPicker.getCategoryService().findCategoryByCid(fileInfoView.getCid(), false);

                    if (!category.getAccessRuleList().stream().allMatch(categoryAccessRule -> categoryAccessRule.getAccessRuleType().equals(AccessConditionTypeEnum.PUBLIC))) {
                        canExpose = false;
                    }
                }

                if (canExpose) {
                    String newUrl = nginxFileNoLinkPicker.nginxUrlPrefix + fileInfoView.returnRelativePath();
                    BasedSequence basedSequence = BasedSequence.of(newUrl);
                    image.setUrl(basedSequence);
                    image.setPageRef(basedSequence);
                }
            }
        }
    }
}
