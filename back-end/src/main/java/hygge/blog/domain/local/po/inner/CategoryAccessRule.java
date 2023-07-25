package hygge.blog.domain.local.po.inner;

import hygge.blog.domain.local.enums.AccessRuleTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

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
@EqualsAndHashCode
@Embeddable
@Schema(title = "文章类别访问规则")
public class CategoryAccessRule {
    @Column(columnDefinition = "enum ('PERSONAL', 'SECRET_KEY', 'GROUP', 'MALE', 'FEMALE', 'CRON', 'PUBLIC') default 'PERSONAL'")
    @Enumerated(EnumType.STRING)
    @Schema(title = "文章类别访问类型")
    private AccessRuleTypeEnum accessRuleType;
    @Column
    @Schema(title = "是否为必要条件")
    private boolean requirement;
    @Column
    @Schema(title = "扩展字段", description = "功能拓展字段，根据规则类型可能为：无意义、秘钥、组标识、时间表达式……")
    private String extendString;
}
