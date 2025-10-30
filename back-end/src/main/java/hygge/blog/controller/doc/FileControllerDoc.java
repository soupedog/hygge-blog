package hygge.blog.controller.doc;

import hygge.blog.controller.base.HyggeBlogController;
import hygge.blog.domain.local.bo.HyggeBlogControllerResponse;
import hygge.blog.domain.local.dto.FileInfoDto;
import hygge.blog.domain.local.dto.FileInfoInfo;
import hygge.blog.domain.local.enums.FileTypeEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * @author Xavier
 * @date 2022/8/1
 */
@Tag(name = "文件 Controller", description = "文件的上传、查询操作")
public interface FileControllerDoc extends HyggeBlogController<ResponseEntity<?>> {
    @Operation(summary = "上传文件", description = "上传文件到特定目录")
    ResponseEntity<HyggeBlogControllerResponse<List<FileInfoDto>>> upload(@Parameter FileTypeEnum fileType, @Parameter(description = "文章类别唯一标识展示用编号") String cid, List<MultipartFile> filesList);

    @Operation(summary = "删除文件", description = "删除数据库中文件及磁盘副本")
    ResponseEntity<HyggeBlogControllerResponse<Void>> deleteFile(String fileNo);

    @Operation(summary = "修改部分文件信息", description = "修改文件名称，描述")
    ResponseEntity<HyggeBlogControllerResponse<Void>> updateFileInfo(String fileNo, Map<String, Object> data);

    @Operation(summary = "查询文件特定信息", description = "根据文件唯一标识查询文件信息")
    ResponseEntity<HyggeBlogControllerResponse<FileInfoDto>> findFileInfo(String fileNo);

    @Operation(summary = "查询文件信息列表", description = "根据文件类型查询文件信息")
    ResponseEntity<HyggeBlogControllerResponse<FileInfoInfo>> findFileInfoList(List<FileTypeEnum> fileTypes, int currentPage, int pageSize);

    @Operation(summary = "对外暴露文件", description = "对外提供文件下载功能")
    ResponseEntity<byte[]> exposeFile(String fileNo);
}
