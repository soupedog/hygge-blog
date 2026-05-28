package hygge.blog.service.local;

import com.vladsch.flexmark.ast.Image;
import com.vladsch.flexmark.formatter.Formatter;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.NodeVisitor;
import com.vladsch.flexmark.util.ast.VisitHandler;
import hygge.blog.domain.local.po.Article;
import hygge.blog.service.local.inner.markdown.impl.ArticleImageVisitorForExpose;
import hygge.blog.service.local.inner.markdown.impl.ArticleImageVisitorForSave;
import org.springframework.stereotype.Service;

/**
 * 处理文章文本内容信息
 *
 * @author Xavier
 * @date 2026/5/28
 */
@Service
public class ArticleContentServiceImpl {
    private final NodeVisitor visitor_for_expose;
    // 原生默认排版美化标准不做额外配置
    private static final Parser parser = Parser.builder().build();
    private static final Formatter formatter = Formatter.builder().build();

    private final FileNoPickerServiceImpl fileNoPickerService;

    public ArticleContentServiceImpl(FileNoPickerServiceImpl fileNoPickerService, FileServiceImpl fileService) {
        this.fileNoPickerService = fileNoPickerService;
        this.visitor_for_expose = new NodeVisitor(
                new VisitHandler<>(Image.class, new ArticleImageVisitorForExpose(fileService))
        );
    }

    public String forSaveContent(Article article) {
        Document markdownDocument = parser.parse(article.getContent());
        getVisitor_for_save(article).visit(markdownDocument);
        return formatter.render(markdownDocument);
    }

    public String forExposeContent(String markdownContent) {
        Document markdownDocument = parser.parse(markdownContent);
        getVisitor_for_expose().visit(markdownDocument);
        return formatter.render(markdownDocument);
    }

    private NodeVisitor getVisitor_for_save(Article article) {
        return new NodeVisitor(
                new VisitHandler<>(Image.class, new ArticleImageVisitorForSave(article, fileNoPickerService))
        );
    }

    private NodeVisitor getVisitor_for_expose() {
        return visitor_for_expose;
    }
}
