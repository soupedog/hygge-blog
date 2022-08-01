package hygge.blog.controller.doc;

import hygge.blog.controller.base.HyggeBlogController;
import hygge.blog.domain.bo.HyggeBlogControllerResponse;
import hygge.blog.domain.dto.FileInfo;
import hygge.blog.domain.enums.FileTypeEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author Xavier
 * @date 2022/8/1
 */
@Tag(name = "文件 Controller", description = "文件的上传、查询操作")
public interface FileControllerDoc extends HyggeBlogController<ResponseEntity<?>> {
    @Operation(summary = "上传文件", description = "上传文件到特定目录")
    ResponseEntity<HyggeBlogControllerResponse<List<FileInfo>>> upload(@Parameter FileTypeEnum fileType, List<MultipartFile> filesList);

    @Operation(summary = "查询文件信息", description = "根据文件类型查询文件信息")
    ResponseEntity<HyggeBlogControllerResponse<List<FileInfo>>> findFileInfo(List<FileTypeEnum> fileTypes);
}
