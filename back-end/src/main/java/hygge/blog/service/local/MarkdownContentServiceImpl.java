package hygge.blog.service.local;

import com.vladsch.flexmark.ast.Image;
import com.vladsch.flexmark.formatter.Formatter;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.NodeVisitor;
import com.vladsch.flexmark.util.ast.VisitHandler;
import hygge.blog.service.local.inner.markdown.ImageResourceReplayVisitor;
import org.springframework.stereotype.Service;

/**
 * @author Xavier
 * @date 2025/11/10
 */
@Service
public class MarkdownContentServiceImpl {
    private final NodeVisitor visitor;
    // 原生默认排版美化标准不做额外配置
    private static final Parser parser = Parser.builder().build();
    private static final Formatter formatter = Formatter.builder().build();

    public MarkdownContentServiceImpl(ImageResourceReplayVisitor imageResourceReplayVisitor) {
        this.visitor = new NodeVisitor(
                new VisitHandler<>(Image.class, imageResourceReplayVisitor)
        );
    }

    public String replaceMarkdownImageResource(String markdownContent) {
        Document markdownDocument = parser.parse(markdownContent);
        visitor.visit(markdownDocument);
        return formatter.render(markdownDocument);
    }
}
