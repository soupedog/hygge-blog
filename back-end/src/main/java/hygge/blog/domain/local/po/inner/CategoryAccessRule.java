package hygge.blog.domain.local.po.inner;

import com.fasterxml.jackson.annotation.JsonInclude;
import hygge.blog.domain.local.enums.AccessRuleTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Xavier
 * @date 2022/7/21
 */
@Getter
@Setter
@Builder
@Generated
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "文章类别访问规则")
// 主动标注作为数据库 json 字段时，null 属性不参与序列化
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryAccessRule {
    @Schema(title = "文章类别访问类型")
    private AccessRuleTypeEnum accessRuleType;
    @Schema(title = "是否为必要条件")
    private boolean requirement;
    @Schema(title = "扩展字段", description = "功能拓展字段，根据规则类型可能为：无意义、秘钥、组标识、时间表达式……")
    private String extendString;
}
