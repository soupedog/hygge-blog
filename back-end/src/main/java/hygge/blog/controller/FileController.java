package hygge.blog.controller;

import hygge.blog.common.HyggeRequestContext;
import hygge.blog.common.HyggeRequestTracker;
import hygge.blog.controller.doc.FileControllerDoc;
import hygge.blog.domain.local.bo.BlogSystemCode;
import hygge.blog.domain.local.bo.HyggeBlogControllerResponse;
import hygge.blog.domain.local.dto.FileInfoDto;
import hygge.blog.domain.local.dto.FileInfoInfo;
import hygge.blog.domain.local.enums.FileTypeEnum;
import hygge.blog.domain.local.po.Category;
import hygge.blog.domain.local.po.FileInfo;
import hygge.blog.domain.local.po.User;
import hygge.blog.domain.local.po.view.FileInfoView;
import hygge.blog.service.local.FileServiceImpl;
import hygge.blog.service.local.normal.CategoryServiceImpl;
import hygge.commons.constant.enums.GlobalHyggeCodeEnum;
import hygge.util.template.HyggeJsonUtilContainer;
import hygge.web.util.log.annotation.ControllerLog;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Xavier
 * @date 2022/8/1
 */
@RestController
@RequestMapping(value = "/blog-service/api/main")
public class FileController extends HyggeJsonUtilContainer implements FileControllerDoc {
    private final FileServiceImpl fileService;
    private final CategoryServiceImpl categoryService;

    private static final byte[] emptyBytes = new byte[0];

    public FileController(FileServiceImpl fileService, CategoryServiceImpl categoryService) {
        this.fileService = fileService;
        this.categoryService = categoryService;
    }

    @Override
    @PostMapping(value = "/file", consumes = "multipart/form-data")
    @ControllerLog(ignoreParamNames = "filesList")
    public ResponseEntity<HyggeBlogControllerResponse<List<FileInfoDto>>> upload(@RequestParam("type") FileTypeEnum fileType,
                                                                                 @RequestParam(value = "cid", required = false) String cid,
                                                                                 @RequestParam("files") List<MultipartFile> filesList) {
        return (ResponseEntity<HyggeBlogControllerResponse<List<FileInfoDto>>>) success(fileService.uploadFile(cid, fileType, filesList));
    }

    @Override
    @DeleteMapping("/file/{fileNo}")
    public ResponseEntity<HyggeBlogControllerResponse<Void>> deleteFile(@PathVariable("fileNo") String fileNo) {
        fileService.deleteFile(fileNo);
        return (ResponseEntity<HyggeBlogControllerResponse<Void>>) success();
    }

    @Override
    @PutMapping("/file/{fileNo}")
    public ResponseEntity<HyggeBlogControllerResponse<Void>> updateFileInfo(@PathVariable("fileNo") String fileNo, @RequestBody Map<String, Object> data) {
        fileService.updateFileInfo(fileNo, data);
        return (ResponseEntity<HyggeBlogControllerResponse<Void>>) success();
    }

    @Override
    @GetMapping("/file")
    @ControllerLog(inputParamEnable = false, outputParamEnable = false)
    public ResponseEntity<HyggeBlogControllerResponse<FileInfoInfo>> findFileInfo(@RequestParam(value = "type", required = false) List<FileTypeEnum> fileTypes,
                                                                                  @RequestParam(required = false, defaultValue = "1") int currentPage,
                                                                                  @RequestParam(required = false, defaultValue = "100") int pageSize) {
        return (ResponseEntity<HyggeBlogControllerResponse<FileInfoInfo>>) success(fileService.findFileInfo(fileTypes, currentPage, pageSize));
    }

    @GetMapping(value = "/file/static/{fileNo}")
    @ControllerLog(outputParamEnable = false)
    public ResponseEntity<byte[]> exposeFile(@PathVariable("fileNo") String fileNo) {
        Optional<FileInfoView> resultTempView = fileService.findFileViewFromDB(fileNo);

        if (resultTempView.isEmpty()) {
            // 为找到 返回 404
            return (ResponseEntity<byte[]>) failWithWrapper(HttpStatus.NOT_FOUND, null, BlogSystemCode.FAIL_TO_QUERY_FILE, null, null, null, emptyResponseWrapper);
        }

        FileInfoView fileInfoView = resultTempView.get();

        // 验证访问权限
        String cid = fileInfoView.getCid();
        if (parameterHelper.isNotEmpty(cid)) {
            HyggeRequestContext context = HyggeRequestTracker.getContext();
            User currentUser = context.getCurrentLoginUser();
            List<Category> categoryList = categoryService.getAccessibleCategoryList(currentUser, null);

            Optional<Category> accessibleCategoryTemp = categoryList.stream().filter(item -> cid.equals(item.getCid())).findAny();

            // 无权访问返回 404
            if (accessibleCategoryTemp.isEmpty()) {
                return (ResponseEntity<byte[]>) failWithWrapper(HttpStatus.NOT_FOUND, null, BlogSystemCode.FAIL_TO_QUERY_FILE, null, null, null, emptyResponseWrapper);
            }
        }
        // 文件下载动作后置，轻量查询鉴权，权限无误再进行笨重操作
        Optional<FileInfo> resultTemp = fileService.findFileFromDB(fileNo);
        // 二次验空，防止上一刻文件还在，下一刻立马被删除
        if (resultTemp.isEmpty()) {
            // 为找到 返回 404
            return (ResponseEntity<byte[]>) failWithWrapper(HttpStatus.NOT_FOUND, null, BlogSystemCode.FAIL_TO_QUERY_FILE, null, null, null, emptyResponseWrapper);
        }

        byte[] result = resultTemp.get().getContent();

        HttpHeaders headers = new HttpHeaders();

        boolean displayDirectly = true;

        switch (fileInfoView.getExtension()) {
            case "png":
                headers.setContentType(MediaType.IMAGE_PNG);
                break;
            case "jpg", "jpeg":
                headers.setContentType(MediaType.IMAGE_JPEG);
                break;
            case "gif":
                headers.setContentType(MediaType.IMAGE_GIF);
                break;
            case "pdf":
                headers.setContentType(MediaType.APPLICATION_PDF);
                break;
            case "mp3":
                headers.setContentType(MediaType.parseMediaType("audio/mpeg"));
                break;
            default:
                displayDirectly = false;
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        }

        String nameWithExtension = fileInfoView.getName() + "." + fileInfoView.getExtension();

        // 影响鼠标右键图片另存为默认文件名称
        if (displayDirectly) {
            // inline() 是在浏览器直接展示
            headers.setContentDisposition(ContentDisposition.inline()
                    .filename(nameWithExtension, StandardCharsets.UTF_8)
                    .build()
            );
        } else {
            // attachment() 模式则是浏览器直接调用下载
            headers.setContentDisposition(ContentDisposition.attachment()
                    .filename(nameWithExtension, StandardCharsets.UTF_8)
                    .build()
            );
        }

        return (ResponseEntity<byte[]>) successWithWrapper(HttpStatus.OK, headers, GlobalHyggeCodeEnum.SUCCESS, null, result, emptyResponseWrapper);
    }
}
