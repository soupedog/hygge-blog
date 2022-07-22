package hygge.blog.domain.po.inner;

import hygge.blog.domain.enums.AccessRuleTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Embeddable
public class CategoryAccessRule {
    /**
     * [PO_STATUS]访问规则类型:仅自己可见,秘钥访问,群组,男性可见,女性可见,周期开放,公开可见
     */
    @Column(columnDefinition = "enum ('PERSONAL', 'SECRET_KEY', 'GROUP', 'MALE', 'FEMALE', 'CRON', 'PUBLIC') default 'PERSONAL'")
    @Enumerated(EnumType.STRING)
    private AccessRuleTypeEnum accessRuleType;
    /**
     * 是否为必要条件
     */
    @Column
    private boolean requirement;
    /**
     * 功能拓展字段，根据规则类型可能为：无意义、秘钥、组标识、时间表达式……
     */
    @Column
    private String extendString;
}
