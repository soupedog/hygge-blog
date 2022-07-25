package hygge.blog.domain.po.base;

import com.vladmihalcea.hibernate.type.json.JsonType;
import hygge.utils.UtilsCreator;
import hygge.utils.definitions.ParameterHelper;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.sql.Timestamp;

/**
 * @author Xavier
 * @date 2022/7/17
 */
@TypeDef(name = "json", typeClass = JsonType.class)
@Getter
@Setter
@Generated
@MappedSuperclass
public abstract class BasePo {
    protected static final ParameterHelper parameterHelper = UtilsCreator.INSTANCE.getDefaultInstance(ParameterHelper.class);

    protected BasePo() {
    }

    @CreationTimestamp
    @Column(nullable = false, updatable = false, columnDefinition = "datetime(3)")
    protected Timestamp createTs;
    @UpdateTimestamp
    @Column(nullable = false, columnDefinition = "datetime(3)")
    protected Timestamp lastUpdateTs;
}
