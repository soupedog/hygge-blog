package hygge.blog.domain.dto.inner;

import hygge.blog.domain.dto.ArticleDto;
import hygge.commons.template.definition.HyggeLogInfoObject;
import hygge.util.UtilCreator;
import hygge.util.definition.CollectionHelper;
import hygge.util.definition.JsonHelper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author Xavier
 * @date 2022/7/27
 */
@Getter
@Setter
@Builder
@Generated
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "文章摘要信息")
public class ArticleSummaryInfo implements HyggeLogInfoObject {
    private static final CollectionHelper collectionHelper = UtilCreator.INSTANCE.getDefaultInstance(CollectionHelper.class);
    private static final JsonHelper<?> jsonHelper = UtilCreator.INSTANCE.getDefaultJsonHelperInstance(false);
    @Schema(title = "文章摘要信息数组")
    private List<ArticleDto> articleSummaryList;
    @Schema(title = "文章总数")
    private long totalCount;

    @Override
    public String toJsonLogInfo() {
        LinkedHashMap<String, Object> logInfo = new LinkedHashMap<>();
        logInfo.put("titleList", collectionHelper.filterNonemptyItemAsArrayList(false, articleSummaryList, ArticleDto::getTitle));
        logInfo.put("totalCount", totalCount);
        return jsonHelper.formatAsString(logInfo);
    }
}
