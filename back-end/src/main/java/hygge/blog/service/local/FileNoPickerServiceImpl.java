package hygge.blog.service.local;

import hygge.blog.service.local.inner.file.FileUrlBuilder;
import hygge.blog.service.local.inner.file.picker.ApiFileNoLinkPicker;
import hygge.blog.service.local.inner.file.picker.NginxFileNoLinkPicker;
import org.springframework.stereotype.Service;

/**
 * @author Xavier
 * @date 2026/5/21
 */
@Service
public class FileNoPickerServiceImpl {
    public final NginxFileNoLinkPicker nginxFileNoLinkPicker;
    public final ApiFileNoLinkPicker apiFileNoLinkPicker;

    public FileNoPickerServiceImpl(FileUrlBuilder fileUrlBuilder, FileServiceImpl fileService) {
        this.nginxFileNoLinkPicker = new NginxFileNoLinkPicker(fileUrlBuilder.nginxUrlPrefix, fileService);
        this.apiFileNoLinkPicker = new ApiFileNoLinkPicker(fileUrlBuilder.apiUrlPrefix);
    }
}
