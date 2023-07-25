package hygge.blog.common.mapper.convert;

import hygge.blog.domain.local.dto.inner.CategoryTreeInfo;
import hygge.blog.domain.local.enums.ArticleStateEnum;
import hygge.blog.domain.local.enums.QuoteStateEnum;
import hygge.blog.domain.local.po.inner.ArticleConfiguration;
import hygge.blog.domain.local.dto.ArticleQuoteSearchCache;
import hygge.web.template.HyggeWebUtilContainer;

import java.sql.Timestamp;

/**
 * @author Xavier
 * @date 2022/8/29
 */
public class SimpleTypeConvert extends HyggeWebUtilContainer {

    public Long timestampAsLong(Timestamp target) {
        return target == null ? null : target.getTime();
    }

    public Timestamp longAsTimestamp(Long target) {
        return target == null ? null : new Timestamp(target);
    }

    public String articleConfigurationAsString(ArticleConfiguration target) {
        return jsonHelper.formatAsString(target);
    }

    public ArticleConfiguration stringAsArticleConfiguration(String target) {
        return jsonHelper.readAsObject(target, ArticleConfiguration.class);
    }

    public String categoryTreeInfoAsString(CategoryTreeInfo target) {
        return jsonHelper.formatAsString(target);
    }

    public CategoryTreeInfo stringAsCategoryTreeInfo(String target) {
        return jsonHelper.readAsObject(target, CategoryTreeInfo.class);
    }

    public ArticleQuoteSearchCache.StateEnum asStateEnum(Enum<?> target) {
        return target == null ? null : ArticleQuoteSearchCache.StateEnum.valueOf(target.name());
    }

    public ArticleStateEnum asArticleStateEnum(Enum<?> target) {
        return target == null ? null : ArticleStateEnum.valueOf(target.name());
    }

    public QuoteStateEnum asQuoteStateEnum(Enum<?> target) {
        return target == null ? null : QuoteStateEnum.valueOf(target.name());
    }
}
