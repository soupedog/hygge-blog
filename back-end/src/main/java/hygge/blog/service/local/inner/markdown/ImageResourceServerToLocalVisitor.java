package hygge.blog.service.local.inner.markdown;

import com.vladsch.flexmark.ast.Image;
import com.vladsch.flexmark.util.ast.Visitor;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import hygge.blog.domain.local.enums.AccessRuleTypeEnum;
import hygge.blog.domain.local.po.Category;
import hygge.blog.domain.local.po.FileInfo;
import hygge.blog.domain.local.po.view.FileInfoView;
import hygge.blog.service.local.FileServiceImpl;
import hygge.blog.service.local.inner.file.FileOperationResult;
import hygge.blog.service.local.inner.file.FileOperationTool;
import hygge.blog.service.local.inner.file.picker.ApiFileNoLinkPicker;
import hygge.blog.service.local.inner.file.picker.NginxFileNoLinkPicker;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * 给 markdown 文档里通过 API 方式对外暴露且类别是公开的图片替换为 本地资源
 *
 * @author Xavier
 * @date 2026/5/21
 */
@Slf4j
public class ImageResourceServerToLocalVisitor implements Visitor<Image> {
    private final String pathPrefix;
    private final ApiFileNoLinkPicker apiFileNoLinkPicker;
    private final NginxFileNoLinkPicker nginxFileNoLinkPicker;

    public ImageResourceServerToLocalVisitor(String pathPrefix, ApiFileNoLinkPicker apiFileNoLinkPicker, NginxFileNoLinkPicker nginxFileNoLinkPicker) {
        // 自动去除结尾反斜杠
        this.pathPrefix = pathPrefix.endsWith("/")
                ? pathPrefix.substring(0, pathPrefix.length() - 1)
                : pathPrefix;
        this.apiFileNoLinkPicker = apiFileNoLinkPicker;
        this.nginxFileNoLinkPicker = nginxFileNoLinkPicker;
    }

    @Override
    public void visit(@NotNull Image image) {
        String rawUrl = image.getUrl().toString();

        String fileNo = apiFileNoLinkPicker.tryToGetFileNo(rawUrl);

        if (fileNo != null) {
            FileOperationResult copyResult = tryToCopyToLocal(fileNo);

            if (FileOperationResult.ResultType.SUCCESS.equals(copyResult.getResultType())) {
                String newSrc = "../images" + copyResult.getExtension().toString();
                BasedSequence basedSequence = BasedSequence.of(newSrc);
                image.setUrl(basedSequence);
                image.setPageRef(basedSequence);
            }

        }
    }

    public FileOperationResult tryToCopyToLocal(String fileNo) {
        // TODO 权限系统改造后这里也得相应处理
        FileServiceImpl fileService = nginxFileNoLinkPicker.getFileService();
        FileInfoView fileInfoView = fileService.findFileViewFromDB(fileNo).orElseGet(null);

        // 匹配上格式，就认为是可替换的目标资源
        if (fileInfoView != null) {
            // 是否可以对外暴露
            boolean canExpose = true;

            if (fileInfoView.getCid() != null) {
                Category category = nginxFileNoLinkPicker.getCategoryService().findCategoryByCid(fileInfoView.getCid(), false);

                if (!category.getAccessRuleList().stream().allMatch(categoryAccessRule -> categoryAccessRule.getAccessRuleType().equals(AccessRuleTypeEnum.PUBLIC))) {
                    canExpose = false;
                }
            }

            if (canExpose) {
                // 正式从数据库查询完整文件
                Optional<FileInfo> fileInfoTemp = fileService.findFileFromDB(fileNo);

                if (fileInfoTemp.isPresent()) {
                    FileInfo fileInfo = fileInfoTemp.get();
                    String relativePath = fileInfo.returnRelativePath();
                    String newPath = pathPrefix + relativePath;

                    FileOperationResult copyResult = FileOperationTool.copyFile(newPath, fileInfo.getName(), fileInfo.getContent());
                    copyResult.setExtension(relativePath);
                    if (FileOperationResult.ResultType.SUCCESS.equals(copyResult.getResultType())) {
                        log.info("File({}) copy to local:{}", fileNo, newPath);
                    } else {
                        log.error(copyResult.getMsg());
                    }

                    return copyResult;
                }
            }
        }
        return null;
    }
}
