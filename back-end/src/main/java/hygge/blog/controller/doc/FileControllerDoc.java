package hygge.blog.controller.doc;

import hygge.blog.controller.base.HyggeBlogController;
import hygge.blog.domain.local.bo.HyggeBlogControllerResponse;
import hygge.blog.domain.local.dto.FileInfoForFrontEnd;
import hygge.blog.domain.local.enums.FileTypeEnum;
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
    ResponseEntity<HyggeBlogControllerResponse<List<FileInfoForFrontEnd>>> upload(@Parameter FileTypeEnum fileType, @Parameter(description = "文章类别唯一标识展示用编号") String cid, List<MultipartFile> filesList);

    @Operation(summary = "查询文件信息", description = "根据文件类型查询文件信息")
    ResponseEntity<HyggeBlogControllerResponse<List<FileInfoForFrontEnd>>> findFileInfo(List<FileTypeEnum> fileTypes, int currentPage, int pageSize);
}
