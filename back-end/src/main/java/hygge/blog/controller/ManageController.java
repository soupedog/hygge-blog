package hygge.blog.controller;

import hygge.blog.common.annotation.RequireAuth;
import hygge.blog.controller.doc.ManageControllerDoc;
import hygge.blog.domain.local.bo.HyggeBlogControllerResponse;
import hygge.blog.service.local.FileCacheRefreshServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;

/**
 * @author Xavier
 * @date 2026/5/21
 */
@RestController
@RequestMapping(value = "/blog-service/api/main")
public class ManageController implements ManageControllerDoc {
    private final FileCacheRefreshServiceImpl fileCacheRefreshService;

    public ManageController(FileCacheRefreshServiceImpl fileCacheRefreshService) {
        this.fileCacheRefreshService = fileCacheRefreshService;
    }

    @Override
    @RequireAuth
    @PostMapping(value = "/refresh/fileCache")
    public ResponseEntity<HyggeBlogControllerResponse<String>> refreshPublicFileCache(@RequestParam(required = false, defaultValue = "false") Boolean forceOverWrite) {
        fileCacheRefreshService.freshAllPublicFileCache(forceOverWrite);
        return (ResponseEntity<HyggeBlogControllerResponse<String>>) success("正在处理:" + new Timestamp(System.currentTimeMillis()));
    }
}
