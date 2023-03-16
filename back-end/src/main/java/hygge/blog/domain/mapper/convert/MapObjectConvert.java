package hygge.blog.domain.mapper.convert;

import com.fasterxml.jackson.core.type.TypeReference;
import hygge.blog.domain.enums.ArticleStateEnum;
import hygge.blog.domain.enums.CategoryStateEnum;
import hygge.blog.domain.enums.CategoryTypeEnum;
import hygge.blog.domain.enums.QuoteStateEnum;
import hygge.blog.domain.enums.TopicStateEnum;
import hygge.blog.domain.enums.UserSexEnum;
import hygge.blog.domain.enums.UserStateEnum;
import hygge.blog.domain.enums.UserTypeEnum;
import hygge.blog.domain.po.inner.ArticleConfiguration;
import hygge.blog.domain.po.inner.CategoryAccessRule;
import hygge.util.UtilCreator;
import hygge.util.definition.JsonHelper;
import hygge.util.definition.ParameterHelper;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Xavier
 * @date 2022/7/18
 */
public class MapObjectConvert {
    private static final ParameterHelper parameterHelper = UtilCreator.INSTANCE.getDefaultInstance(ParameterHelper.class);
    private static final JsonHelper<?> jsonHelper = UtilCreator.INSTANCE.getDefaultJsonHelperInstance(false);

    private static TypeReference<ArrayList<CategoryAccessRule>> TYPE_INFO_ACCESS_RULE_LIST = new TypeReference<>() {
    };

    public String asString(Object target) {
        return parameterHelper.string(target);
    }

    public Integer asInteger(Object target) {
        return parameterHelper.integerFormat("target", target);
    }

    public Long asLong(Object target) {
        return parameterHelper.longFormat("target", target);
    }

    public Timestamp asTimestamp(Object target) {
        Long longValue = parameterHelper.longFormat("target", target);
        return longValue == null ? null : new Timestamp(longValue);
    }

    public UserSexEnum asUserSexEnum(Object target) {
        return UserSexEnum.parse(parameterHelper.string(target));
    }

    public UserTypeEnum asUserTypeEnum(Object target) {
        return UserTypeEnum.parse(parameterHelper.string(target));
    }

    public UserStateEnum asUserStateEnum(Object target) {
        return UserStateEnum.parse(parameterHelper.string(target));
    }

    public TopicStateEnum asTopicStateEnum(Object target) {
        return TopicStateEnum.parse(parameterHelper.string(target));
    }

    public List<CategoryAccessRule> asAccessRuleList(Object target) {
        if (target == null) {
            return null;
        }
        return (List<CategoryAccessRule>) jsonHelper.readAsObjectWithClassInfo(jsonHelper.formatAsString(target), TYPE_INFO_ACCESS_RULE_LIST);
    }

    public CategoryTypeEnum asCategoryTypeEnum(Object target) {
        return CategoryTypeEnum.parse(parameterHelper.string(target));
    }

    public CategoryStateEnum asCategoryStateEnum(Object target) {
        return CategoryStateEnum.parse(parameterHelper.string(target));
    }


    public ArticleConfiguration asArticleConfiguration(Object target) {
        if (target == null) {
            return null;
        }
        return jsonHelper.readAsObject(jsonHelper.formatAsString(target), ArticleConfiguration.class);
    }

    public ArticleStateEnum asArticleStateEnum(Object target) {
        return ArticleStateEnum.parse(parameterHelper.string(target));
    }

    public QuoteStateEnum asQuoteStateEnum(Object target) {
        return QuoteStateEnum.parse(parameterHelper.string(target));
    }
}
