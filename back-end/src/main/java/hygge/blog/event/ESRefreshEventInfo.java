package hygge.blog.event;

import hygge.blog.domain.local.dto.ArticleQuoteSearchCache;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Xavier
 * @date 2025/9/1
 */
@Getter
@Setter
public class ESRefreshEventInfo {
    private ArticleQuoteSearchCache.Type type;
    private Integer articleId;
    private Integer quoteId;
    /**
     * 是否为全体刷新，默认未单个刷新。为 true 时，自主拉取并刷新目标 type 的所有对象
     */
    private boolean isForAll;

    public ESRefreshEventInfo(ArticleQuoteSearchCache.Type type, boolean isForAll, Integer uniqueId) {
        this.type = type;
        this.isForAll = isForAll;
        if (!isForAll) {
            // 仅在是单个刷新时才有必要记录目标刷新对象唯一标识
            if (type == ArticleQuoteSearchCache.Type.QUOTE) {
                this.quoteId = uniqueId;
            } else {
                this.articleId = uniqueId;
            }
        }
    }
}
