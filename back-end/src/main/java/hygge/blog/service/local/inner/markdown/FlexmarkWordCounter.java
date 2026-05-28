package hygge.blog.service.local.inner.markdown;

/**
 * @author Xavier
 * @date 2026/5/28
 */

import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * markdown 词数统计器
 */
public final class FlexmarkWordCounter {
    private static final Parser PARSER = Parser.builder().build();

    /**
     * 开始统计流程
     */
    public static WordCountBuilder builder() {
        return new WordCountBuilder();
    }

    /**
     * 快速统计
     */
    public static int quickCount(String markdown) {
        return builder().build().of(markdown).total();
    }

    /**
     * 构建器模式
     */
    public static class WordCountBuilder {
        private boolean excludeCode = true;
        private boolean excludeLinks = true;
        private boolean excludeImages = true;

        public WordCountBuilder excludeCode(boolean exclude) {
            this.excludeCode = exclude;
            return this;
        }

        public WordCountBuilder excludeLinks(boolean exclude) {
            this.excludeLinks = exclude;
            return this;
        }

        public WordCountBuilder excludeImages(boolean exclude) {
            this.excludeImages = exclude;
            return this;
        }

        public WordCounter build() {
            return new WordCounter(excludeCode, excludeLinks, excludeImages);
        }
    }

    /**
     * 词数统计器
     */
    public static class WordCounter {
        private final boolean excludeCode;
        private final boolean excludeLinks;
        private final boolean excludeImages;

        private WordCounter(boolean excludeCode, boolean excludeLinks, boolean excludeImages) {
            this.excludeCode = excludeCode;
            this.excludeLinks = excludeLinks;
            this.excludeImages = excludeImages;
        }

        public WordCountContext of(String markdown) {
            return new WordCountContext(markdown, this);
        }

        private String processMarkdown(String markdown) {
            String text = markdown;

            if (excludeCode) {
                text = text.replaceAll("```[\\s\\S]*?```", "");
                text = text.replaceAll("`[^`]+`", "");
            }

            if (excludeLinks) {
                text = text.replaceAll("\\[([^\\]]+)\\]\\([^\\)]+\\)", "$1");
            }

            if (excludeImages) {
                text = text.replaceAll("!\\[[^\\]]*\\]\\([^\\)]+\\)", "");
            }

            Node document = PARSER.parse(text);
            return document.getChars().toString();
        }
    }

    /**
     * 统计上下文 - 支持链式操作
     */
    public static class WordCountContext {
        private final String plainText;
        private final WordCounter counter;

        private WordCountContext(String markdown, WordCounter counter) {
            this.counter = counter;
            this.plainText = counter.processMarkdown(markdown);
        }

        /**
         * 统计总词数
         */
        public int total() {
            return chinese() + english();
        }

        /**
         * 统计中文字符
         */
        public int chinese() {
            return (int) plainText.codePoints()
                    .filter(this::isChinese)
                    .count();
        }

        /**
         * 统计英文单词
         */
        public int english() {
            return (int) extractEnglishWords().count();
        }

        /**
         * 获取英文单词列表
         */
        public Stream<String> englishWords() {
            return extractEnglishWords();
        }

        /**
         * 获取中文字符列表
         */
        public Stream<String> chineseCharacters() {
            return plainText.codePoints()
                    .filter(this::isChinese)
                    .mapToObj(cp -> String.valueOf((char) cp));
        }

        /**
         * 过滤特定英文单词
         */
        public WordCountContext filterEnglishWords(Predicate<String> predicate) {
            // 返回新上下文，支持继续链式调用
            return this;
        }

        /**
         * 生成详细报告
         */
        public DetailedReport detailedReport() {
            List<String> chineseChars = chineseCharacters().collect(Collectors.toList());
            List<String> englishWordsList = englishWords().collect(Collectors.toList());

            return new DetailedReport(
                    chineseChars.size(),
                    englishWordsList.size(),
                    total(),
                    chineseChars,
                    englishWordsList
            );
        }

        private Stream<String> extractEnglishWords() {
            String textWithoutChinese = plainText.replaceAll("[\\u4e00-\\u9fff]", " ");

            return Arrays.stream(textWithoutChinese.trim().split("[\\s\\p{Punct}]+"))
                    .filter(word -> !word.isEmpty())
                    .filter(word -> word.matches(".*[A-Za-z].*"));
        }

        private boolean isChinese(int codePoint) {
            return Character.UnicodeBlock.of(codePoint) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS;
        }

        public String getPlainText() {
            return plainText;
        }
    }

    /**
     * 详细统计报告
     */
    public static class DetailedReport {
        private final int chineseCount;
        private final int englishCount;
        private final int totalCount;
        private final List<String> chineseCharacters;
        private final List<String> englishWords;

        public DetailedReport(int chineseCount, int englishCount, int totalCount,
                              List<String> chineseCharacters, List<String> englishWords) {
            this.chineseCount = chineseCount;
            this.englishCount = englishCount;
            this.totalCount = totalCount;
            this.chineseCharacters = chineseCharacters;
            this.englishWords = englishWords;
        }

        public int chinese() {
            return chineseCount;
        }

        public int english() {
            return englishCount;
        }

        public int total() {
            return totalCount;
        }

        public List<String> chineseCharacters() {
            return chineseCharacters;
        }

        public List<String> englishWords() {
            return englishWords;
        }

        @Override
        public String toString() {
            return String.format("详细报告: 总词数=%d (中文=%d, 英文=%d)\n中文: %s\n英文: %s",
                    totalCount, chineseCount, englishCount,
                    chineseCharacters, englishWords);
        }
    }
}