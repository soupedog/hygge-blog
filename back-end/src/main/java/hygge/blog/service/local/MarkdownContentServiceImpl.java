package hygge.blog.service.local;

import com.vladsch.flexmark.ast.Image;
import com.vladsch.flexmark.formatter.Formatter;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.NodeVisitor;
import com.vladsch.flexmark.util.ast.VisitHandler;
import hygge.blog.service.local.inner.markdown.ImageResourceApiToNginxVisitor;
import hygge.blog.service.local.inner.markdown.ImageResourceNginxToApiVisitor;
import hygge.blog.service.local.inner.markdown.ImageResourceOneTimeAuthorizationVisitor;
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
    // 原生默认排版美化标准不做额外配置
    private static final Parser parser = Parser.builder().build();
    private static final Formatter formatter = Formatter.builder().build();

    public MarkdownContentServiceImpl(ImageResourceOneTimeAuthorizationVisitor imageResourceOneTimeAuthorizationVisitor,
                                      ImageResourceApiToNginxVisitor imageResourceApiToNginxVisitor,
                                      ImageResourceNginxToApiVisitor imageResourceNginxToApiVisitor
    ) {
        this.visitor_OneTimeAuthorization = new NodeVisitor(
                new VisitHandler<>(Image.class, imageResourceOneTimeAuthorizationVisitor)
        );
        this.visitor_ResourceApiToNginx = new NodeVisitor(
                new VisitHandler<>(Image.class, imageResourceApiToNginxVisitor)
        );
        this.visitor_ResourceNginxToApi = new NodeVisitor(
                new VisitHandler<>(Image.class, imageResourceNginxToApiVisitor)
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
}
