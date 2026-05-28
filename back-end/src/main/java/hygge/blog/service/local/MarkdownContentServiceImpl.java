package hygge.blog.service.local;

import com.vladsch.flexmark.ast.Image;
import com.vladsch.flexmark.formatter.Formatter;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.NodeVisitor;
import com.vladsch.flexmark.util.ast.VisitHandler;
import com.vladsch.flexmark.util.ast.Visitor;
import hygge.blog.service.local.inner.file.CacheFileKeyKeeper;
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

    private final FileNoPickerServiceImpl fileNoPickerService;

    public MarkdownContentServiceImpl(FileNoPickerServiceImpl fileNoPickerService, CacheFileKeyKeeper fileKeyKeeper) {
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
}
