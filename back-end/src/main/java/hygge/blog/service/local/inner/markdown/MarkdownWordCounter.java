package hygge.blog.service.local.inner.markdown;

import com.vladsch.flexmark.ast.Code;
import com.vladsch.flexmark.ast.FencedCodeBlock;
import com.vladsch.flexmark.ast.Image;
import com.vladsch.flexmark.ast.IndentedCodeBlock;
import com.vladsch.flexmark.ast.Link;
import com.vladsch.flexmark.ast.Text;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.sequence.BasedSequence;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 基于 Flexmark 的 Markdown 字数统计器
 * 统计规则：中文字符数 + 英文单词数
 */
public class MarkdownWordCounter {
    private static final Pattern CHINESE_PATTERN = Pattern.compile("[\\u4e00-\\u9fff]");
    private static final Pattern ENGLISH_WORD_PATTERN = Pattern.compile("[a-zA-Z]+");

    /**
     * 统计 Markdown 文本的字数（中文字符 + 英文单词数，排除代码）
     *
     * @param markdown Markdown 原始文本
     * @return 总字数
     */
    public static int count(String markdown) {
        if (markdown == null || markdown.isEmpty()) {
            return 0;
        }

        // 1. 解析 Markdown 为 AST
        Parser parser = Parser.builder().build();
        Document document = parser.parse(markdown);

        // 2. 提取所有可见文本（跳过代码）
        String plainText = extractText(document);

        // 3. 统计中文字符
        int chineseCount = 0;
        Matcher chineseMatcher = CHINESE_PATTERN.matcher(plainText);
        while (chineseMatcher.find()) {
            chineseCount++;
        }

        // 4. 统计英文单词
        int englishWordCount = 0;
        Matcher wordMatcher = ENGLISH_WORD_PATTERN.matcher(plainText);
        while (wordMatcher.find()) {
            englishWordCount++;
        }

        return chineseCount + englishWordCount;
    }

    /**
     * 递归遍历 AST 节点，提取文本（忽略代码块和内联代码）
     */
    private static String extractText(Node node) {
        StringBuilder sb = new StringBuilder();

        // 直接跳过任何代码节点（内联代码、围栏代码块、缩进代码块）
        if (node instanceof Code || node instanceof FencedCodeBlock || node instanceof IndentedCodeBlock) {
            return ""; // 不提取内容
        }

        if (node instanceof Text) {
            // 普通文本（段落文字、链接文字、强调文字等）
            sb.append(node.getChars());
        }

        if (node instanceof Link) {
            BasedSequence title = ((Link) node).getTitle();
            if (title != null && !title.isEmpty()) {
                sb.append(title);
            }
        }

        if (node instanceof Image) {
            BasedSequence title = ((Image) node).getTitle();
            if (title != null && !title.isEmpty()) {
                sb.append(title);
            }
        }

        // 递归处理所有子节点
        Node child = node.getFirstChild();
        while (child != null) {
            sb.append(extractText(child));
            child = child.getNext();
        }

        return sb.toString();
    }
}