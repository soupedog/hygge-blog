package hygge.blog.service.local;

import com.vladsch.flexmark.ast.Image;
import com.vladsch.flexmark.formatter.Formatter;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.NodeVisitor;
import com.vladsch.flexmark.util.ast.VisitHandler;
import com.vladsch.flexmark.util.ast.Visitor;
import hygge.blog.service.local.inner.file.CacheFileKeyKeeper;
import hygge.blog.service.local.inner.markdown.ImageResourceApiToNginxVisitor;
import hygge.blog.service.local.inner.markdown.ImageResourceNginxToApiVisitor;
import hygge.blog.service.local.inner.markdown.ImageResourceOldToApiVisitor;
import hygge.blog.service.local.inner.markdown.ImageResourceOneTimeAuthorizationVisitor;
import hygge.blog.service.local.inner.markdown.ImageResourceServerToLocalVisitor;
import org.springframework.stereotype.Service;

/**
 * @author Xavier
 * @date 2025/11/10
 */
@Service
public class MarkdownContentServiceImpl {
    // 原生默认排版美化标准不做额外配置
    private static final Parser parser = Parser.builder().build();
    private static final Formatter formatter = Formatter.builder().build();

    private final NodeVisitor visitor_OneTimeAuthorization;
    private final NodeVisitor visitor_ResourceApiToNginx;
    private final NodeVisitor visitor_ResourceNginxToApi;
    private final NodeVisitor visitor_ResourceOldToApi;
    private final FileNoPickerServiceImpl fileNoPickerService;

    public MarkdownContentServiceImpl(FileNoPickerServiceImpl fileNoPickerService, CacheFileKeyKeeper fileKeyKeeper) {
        this.visitor_OneTimeAuthorization = new NodeVisitor(
                new VisitHandler<>(Image.class, new ImageResourceOneTimeAuthorizationVisitor(fileNoPickerService.apiFileNoLinkPicker, fileKeyKeeper))
        );
        this.visitor_ResourceApiToNginx = new NodeVisitor(
                new VisitHandler<>(Image.class, new ImageResourceApiToNginxVisitor(fileNoPickerService.apiFileNoLinkPicker, fileNoPickerService.nginxFileNoLinkPicker))
        );
        this.visitor_ResourceNginxToApi = new NodeVisitor(
                new VisitHandler<>(Image.class, new ImageResourceNginxToApiVisitor(fileNoPickerService.apiFileNoLinkPicker, fileNoPickerService.nginxFileNoLinkPicker))
        );
        this.visitor_ResourceOldToApi = new NodeVisitor(
                new VisitHandler<>(Image.class, new ImageResourceOldToApiVisitor(fileNoPickerService.apiFileNoLinkPicker, fileNoPickerService.apiFileNoLinkPicker_old, fileNoPickerService.nginxFileNoLinkPicker_old))
        );
        this.fileNoPickerService = fileNoPickerService;
    }

    public NodeVisitor getImageNodeVisitor(Visitor<Image> visitor) {
        return new NodeVisitor(
                new VisitHandler<>(Image.class, visitor)
        );
    }

    public ImageResourceServerToLocalVisitor getImageResourceServerToLocalVisitor(String pathPrefix) {
        return new ImageResourceServerToLocalVisitor(pathPrefix, fileNoPickerService.apiFileNoLinkPicker, fileNoPickerService.nginxFileNoLinkPicker);
    }

    public String markdownServerToLocal(NodeVisitor serverToLocalVisitor, String markdownContent) {
        Document markdownDocument = parser.parse(markdownContent);
        serverToLocalVisitor.visit(markdownDocument);
        return formatter.render(markdownDocument);
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
