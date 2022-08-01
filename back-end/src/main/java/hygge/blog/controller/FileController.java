package hygge.blog.controller;

import hygge.blog.controller.doc.FileControllerDoc;
import hygge.blog.domain.bo.HyggeBlogControllerResponse;
import hygge.blog.domain.dto.FileInfo;
import hygge.blog.domain.enums.FileTypeEnum;
import hygge.blog.service.FileServiceImpl;
import hygge.web.utils.log.annotation.ControllerLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author Xavier
 * @date 2022/8/1
 */
@RestController
@RequestMapping(value = "/blog-service/api/main")
public class FileController implements FileControllerDoc {
    @Autowired
    private FileServiceImpl fileService;

    @Override
    @PostMapping(value = "/file", consumes = "multipart/form-data")
    @ControllerLog(ignoreParamNames = "filesList")
    public ResponseEntity<HyggeBlogControllerResponse<List<FileInfo>>> upload(@RequestParam("type") FileTypeEnum fileType, @RequestParam("files") List<MultipartFile> filesList) {
        return (ResponseEntity<HyggeBlogControllerResponse<List<FileInfo>>>) success(fileService.uploadFile(fileType, filesList));
    }

    @Override
    @GetMapping("/file")
    public ResponseEntity<HyggeBlogControllerResponse<List<FileInfo>>> findFileInfo(@RequestParam("type") List<FileTypeEnum> fileTypes) {
        return (ResponseEntity<HyggeBlogControllerResponse<List<FileInfo>>>) success(fileService.findFileInfo(fileTypes));
    }
}
