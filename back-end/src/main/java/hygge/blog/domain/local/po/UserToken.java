package hygge.blog.domain.local.po;

import hygge.blog.domain.local.enums.TokenScopeEnum;
import hygge.util.UtilCreator;
import hygge.util.definition.RandomHelper;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

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
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "user_token",
        indexes = {@Index(name = "index_userId_scope", columnList = "userId,scope", unique = true)}
)
public class UserToken {
    private static final RandomHelper randomHelper = UtilCreator.INSTANCE.getDefaultInstance(RandomHelper.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer tokenId;
    @Column(nullable = false)
    private Integer userId;
    @Column(nullable = false, columnDefinition = "enum ('WEB', 'PHONE') default 'WEB'")
    @Enumerated(EnumType.STRING)
    private TokenScopeEnum scope;
    @Column(nullable = false)
    private String token;
    @Column(nullable = false)
    private String refreshKey;
    @Column(nullable = false, columnDefinition = "datetime(3)")
    private Timestamp deadline;

    public void refresh(long currentTimeStamp) {
        this.token = randomHelper.getUniversallyUniqueIdentifier(true);
        this.refreshKey = randomHelper.getUniversallyUniqueIdentifier(true);

        long nextDeadline = Instant.ofEpochMilli(currentTimeStamp)
                .plus(3, ChronoUnit.HOURS)
                .toEpochMilli();
        // 往后有效 3 小时
        this.deadline = new Timestamp(nextDeadline);
    }
}
