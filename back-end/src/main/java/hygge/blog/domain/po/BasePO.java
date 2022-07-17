package hygge.blog.domain.po;

import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.sql.Timestamp;

/**
 * @author Xavier
 * @date 2022/7/17
 */
@Getter
@Setter
@Generated
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BasePO {
    protected BasePO() {
    }

    @CreatedDate
    @Column(nullable = false)
    private Timestamp createTs;
    @LastModifiedDate
    @Column(nullable = false)
    private Timestamp lastUpdateTs;
}
