package hygge.blog.domain.local.po;

import hygge.blog.domain.local.po.base.BasePo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.List;

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
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "blog_group")
public class BlogGroup extends BasePo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer groupId;
    /**
     * [PO_PK_ALIAS]群组展示用唯一标识
     */
    @Column(nullable = false)
    private String gid;
    /**
     * 群组名称
     */
    @Column(nullable = false, unique = true)
    private String groupName;
    /**
     * 群组拥有者唯一标识
     */
    @Column(nullable = false)
    private Integer userId;
    /**
     * 组内成员
     */
    @ManyToMany
    @JoinTable(name = "join_user_blog_group",
            // name:referencedColumnName 在中间表的别名  referencedColumnName:当前表关联字段名称
            joinColumns = {@JoinColumn(name = "groupId", referencedColumnName = "groupId")},
            // 关联关系另一方，其他属性同 joinColumns
            inverseJoinColumns = {@JoinColumn(name = "userId", referencedColumnName = "userId")}
    )
    private List<User> members;
}
