package hygge.blog.domain.po;

import hygge.blog.domain.enums.TokenScopeEnum;
import hygge.utils.UtilsCreator;
import hygge.utils.definitions.RandomHelper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.sql.Timestamp;

/**
 * @author Xavier
 * @date 2022/7/19
 */
@Getter
@Setter
@Builder
@Generated
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
@Entity
@Table(name = "user_token", uniqueConstraints = {@UniqueConstraint(columnNames = {"userId", "scope"})})
public class UserToken {
    private static final RandomHelper randomHelper = UtilsCreator.INSTANCE.getDefaultInstance(RandomHelper.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer tokenId;
    @Column(nullable = false)
    private Integer userId;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TokenScopeEnum scope;
    @Column(nullable = false)
    private String token;
    @Column(nullable = false)
    private String refreshKey;
    @Column(nullable = false)
    private Timestamp deadline;

    public void refresh(long currentTimeStamp) {
        this.token = randomHelper.getUniversallyUniqueIdentifier(false);
        this.refreshKey = randomHelper.getUniversallyUniqueIdentifier(false);
        // 往后有效 3 小时
        this.deadline = new Timestamp(currentTimeStamp + 10800000L);
    }
}
