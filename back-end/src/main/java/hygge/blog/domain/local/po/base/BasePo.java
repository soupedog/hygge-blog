package hygge.blog.domain.local.po.base;

import hygge.util.UtilCreator;
import hygge.util.definition.ParameterHelper;
import hygge.util.definition.UnitConvertHelper;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

/**
 * @author Xavier
 * @date 2022/7/17
 */
@Getter
@Setter
@Generated
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@MappedSuperclass
public abstract class BasePo {
    protected static final ParameterHelper parameterHelper = UtilCreator.INSTANCE.getDefaultInstance(ParameterHelper.class);
    protected static final UnitConvertHelper unitConvertHelper = UtilCreator.INSTANCE.getDefaultInstance(UnitConvertHelper.class);

    @CreationTimestamp
    @Column(nullable = false, updatable = false, columnDefinition = "datetime(3)")
    protected Timestamp createTs;
    @UpdateTimestamp
    @Column(nullable = false, columnDefinition = "datetime(3)")
    protected Timestamp lastUpdateTs;
}
