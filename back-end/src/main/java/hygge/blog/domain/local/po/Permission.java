package hygge.blog.domain.local.po;

import hygge.blog.domain.local.po.base.BasePo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

/**
 * 权限
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
@Table(name = "permission")
public class Permission extends BasePo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "permissionId")
    private Integer permissionId;
    /**
     * 拥有者唯一标识(拥有者本人默认直接获得授权)
     */
    @Column(nullable = false)
    private Integer userId;
    /**
     * 权限名称，如 "管理员写权限"、"文章删除权限"
     */
    @Column(nullable = false)
    private String name;
    /**
     * {@link AccessCondition} 对应表的主键列表
     * 存储格式：JSON 数组字符串，如 "[1, 2, 3]"
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private List<Integer> acIdList;
    /**
     * 权限描述
     */
    @Column
    private String description;

    public boolean isOwnerOfTargetUser(Integer targetUserId) {
        if (targetUserId == null) {
            return false;
        }
        return userId.equals(targetUserId);
    }
}
