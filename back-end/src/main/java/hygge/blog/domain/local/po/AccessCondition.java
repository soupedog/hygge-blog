package hygge.blog.domain.local.po;

import hygge.blog.domain.local.enums.AccessConditionTypeEnum;
import hygge.blog.domain.local.po.base.BasePo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

/**
 * 访问条件，满足该条件说明有 XX 权限
 *
 * @author Xavier
 * @date 2026/5/24
 */
@Getter
@Setter
@Builder
@Generated
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "access_condition")
public class AccessCondition extends BasePo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer acId;
    /**
     * 类型
     */
    @Column(insertable = false, columnDefinition = "enum ('PERSONAL', 'SECRET_KEY', 'GROUP', 'ROLE', 'SEX', 'CRON', 'PUBLIC') default 'SECRET_KEY'")
    @Enumerated(EnumType.STRING)
    private AccessConditionTypeEnum type;
    /**
     * 是否为必要条件(如果为 True，则当前规则有一票否决权，仅当前规则不满足也会判定为没有权限)
     */
    private boolean requirement;
    /**
     * 功能拓展字段，根据规则类型可能为：无意义、秘钥、组标识、时间表达式……
     */
    private String extendString;
}
