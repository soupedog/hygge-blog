package hygge.blog.service.local;

import com.vladsch.flexmark.ast.Image;
import com.vladsch.flexmark.formatter.Formatter;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.NodeVisitor;
import com.vladsch.flexmark.util.ast.VisitHandler;
import hygge.blog.service.local.inner.file.CacheFileKeyKeeper;
import hygge.blog.service.local.inner.file.picker.ApiFileNoLinkPicker;
import hygge.blog.service.local.inner.file.picker.NginxFileNoLinkPicker;
import hygge.blog.service.local.inner.markdown.ImageResourceApiToNginxVisitor;
import hygge.blog.service.local.inner.markdown.ImageResourceNginxToApiVisitor;
import hygge.blog.service.local.inner.markdown.ImageResourceOldToApiVisitor;
import hygge.blog.service.local.inner.markdown.ImageResourceOneTimeAuthorizationVisitor;
import hygge.blog.service.local.normal.CategoryServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author Xavier
 * @date 2025/11/10
 */
@Service
public class MarkdownContentServiceImpl {
    private final NodeVisitor visitor_OneTimeAuthorization;
    private final NodeVisitor visitor_ResourceApiToNginx;
    private final NodeVisitor visitor_ResourceNginxToApi;
    private final NodeVisitor visitor_ResourceOldToApi;

    public final NginxFileNoLinkPicker nginxFileNoLinkPicker;
    public final NginxFileNoLinkPicker nginxFileNoLinkPicker_old;
    public final ApiFileNoLinkPicker apiFileNoLinkPicker;
    public final ApiFileNoLinkPicker apiFileNoLinkPicker_old;

    // 原生默认排版美化标准不做额外配置
    private static final Parser parser = Parser.builder().build();
    private static final Formatter formatter = Formatter.builder().build();

    public MarkdownContentServiceImpl(
            @Value("${hyyge.blog.file.expose.api.prefix}") String apiUrlPrefix,
            @Value("${hyyge.blog.file.expose.old.api.prefix:old.com/}") String apiUrlPrefix_old,
            @Value("${hyyge.blog.file.expose.nginx.prefix}") String nginxUrlPrefix,
            @Value("${hyyge.blog.file.expose.old.nginx.prefix:old.com/static/}") String nginxUrlPrefix_old,
            FileServiceImpl fileService,
            CacheFileKeyKeeper fileKeyKeeper,
            CategoryServiceImpl categoryService
    ) {
        this.nginxFileNoLinkPicker = new NginxFileNoLinkPicker(nginxUrlPrefix, fileService);
        this.nginxFileNoLinkPicker_old = new NginxFileNoLinkPicker(nginxUrlPrefix_old, fileService);
        this.apiFileNoLinkPicker = new ApiFileNoLinkPicker(apiUrlPrefix);
        this.apiFileNoLinkPicker_old = new ApiFileNoLinkPicker(apiUrlPrefix_old);

        this.visitor_OneTimeAuthorization = new NodeVisitor(
                new VisitHandler<>(Image.class, new ImageResourceOneTimeAuthorizationVisitor(apiFileNoLinkPicker, fileKeyKeeper))
        );
        this.visitor_ResourceApiToNginx = new NodeVisitor(
                new VisitHandler<>(Image.class, new ImageResourceApiToNginxVisitor(apiFileNoLinkPicker, nginxFileNoLinkPicker, fileService, categoryService))
        );
        this.visitor_ResourceNginxToApi = new NodeVisitor(
                new VisitHandler<>(Image.class, new ImageResourceNginxToApiVisitor(apiFileNoLinkPicker, nginxFileNoLinkPicker))
        );
        this.visitor_ResourceOldToApi = new NodeVisitor(
                new VisitHandler<>(Image.class, new ImageResourceOldToApiVisitor(apiFileNoLinkPicker, apiFileNoLinkPicker_old, nginxFileNoLinkPicker_old))
        );
    }

    public String markdownOneTimeAuthorization(String markdownContent) {
        Document markdownDocument = parser.parse(markdownContent);
        visitor_OneTimeAuthorization.visit(markdownDocument);
        return formatter.render(markdownDocument);
    }

    public String markdownImageResourceApiToNginx(String markdownContent) {
        Document markdownDocument = parser.parse(markdownContent);
        visitor_ResourceApiToNginx.visit(markdownDocument);
        return formatter.render(markdownDocument);
    }

    public String markdownImageResourceNginxToApi(String markdownContent) {
        Document markdownDocument = parser.parse(markdownContent);
        visitor_ResourceNginxToApi.visit(markdownDocument);
        return formatter.render(markdownDocument);
    }

    public String markdownImageResourceOldToApi(String markdownContent) {
        Document markdownDocument = parser.parse(markdownContent);
        visitor_ResourceOldToApi.visit(markdownDocument);
        return formatter.render(markdownDocument);
    }
}
