package hygge.blog.service.local;

import hygge.blog.service.local.inner.file.picker.ApiFileNoLinkPicker;
import hygge.blog.service.local.inner.file.picker.NginxFileNoLinkPicker;
import hygge.blog.service.local.normal.CategoryServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author Xavier
 * @date 2026/5/21
 */
@Service
public class FileNoPickerServiceImpl {
    public final NginxFileNoLinkPicker nginxFileNoLinkPicker;
    public final NginxFileNoLinkPicker nginxFileNoLinkPicker_old;
    public final ApiFileNoLinkPicker apiFileNoLinkPicker;
    public final ApiFileNoLinkPicker apiFileNoLinkPicker_old;

    public FileNoPickerServiceImpl(
            @Value("${hyyge.blog.file.expose.api.prefix}") String apiUrlPrefix,
            @Value("${hyyge.blog.file.expose.old.api.prefix:old.com/}") String apiUrlPrefix_old,
            @Value("${hyyge.blog.file.expose.nginx.prefix}") String nginxUrlPrefix,
            @Value("${hyyge.blog.file.expose.old.nginx.prefix:old.com/static/}") String nginxUrlPrefix_old,
            FileServiceImpl fileService,
            CategoryServiceImpl categoryService
    ) {
        this.nginxFileNoLinkPicker = new NginxFileNoLinkPicker(nginxUrlPrefix, fileService, categoryService);
        this.nginxFileNoLinkPicker_old = new NginxFileNoLinkPicker(nginxUrlPrefix_old, fileService, categoryService);
        this.apiFileNoLinkPicker = new ApiFileNoLinkPicker(apiUrlPrefix);
        this.apiFileNoLinkPicker_old = new ApiFileNoLinkPicker(apiUrlPrefix_old);
    }

}
