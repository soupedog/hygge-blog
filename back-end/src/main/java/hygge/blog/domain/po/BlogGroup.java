package hygge.blog.domain.po;

import hygge.blog.domain.po.base.BasePO;
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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
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
public class BlogGroup extends BasePO {
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
    @JoinColumn(nullable = false, name = "userId")
    @ManyToOne(targetEntity = User.class)
    private Integer userId;
    /**
     * 组内成员
     */
    @ManyToMany(mappedBy = "blogGroupList")
    private List<User> members;
}
