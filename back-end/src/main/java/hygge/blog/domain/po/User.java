package hygge.blog.domain.po;

import hygge.blog.domain.enums.UserSexEnum;
import hygge.blog.domain.enums.UserStateEnum;
import hygge.blog.domain.enums.UserTypeEnum;
import hygge.blog.domain.po.base.BasePo;
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
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.List;

/**
 * @author Xavier
 * @date 2022/7/17
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
@Table(name = "user", indexes = {@Index(name = "index_uid", columnList = "uid", unique = true)})
public class User extends BasePo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer userId;
    /**
     * [PO_PK_ALIAS]用户展示用唯一标识
     */
    @Column(length = 15)
    private String uid;
    /**
     * 用户身份
     */
    @Column(insertable = false, columnDefinition = "enum ('ROOT', 'NORMAL') default 'NORMAL'")
    @Enumerated(EnumType.STRING)
    private UserTypeEnum userType;
    /**
     * 登录密码
     */
    @Column(nullable = false)
    private String password;
    /**
     * 用户名
     */
    @Column(unique = true, nullable = false)
    private String userName;
    /**
     * 用户头像链接
     */
    @Column(nullable = false)
    private String userAvatar;
    /**
     * 用户性别:保密,男,女
     */
    @Column(columnDefinition = "enum ('SECRET', 'MAN', 'WOMAN') default 'SECRET'")
    @Enumerated(EnumType.STRING)
    private UserSexEnum userSex;
    /**
     * 个人简介
     */
    @Column(columnDefinition = "varchar(500) default '系统免费赠送给每一份小可爱的简介'")
    private String biography;
    /**
     * 生日 UTC 毫秒级 Long 时间戳
     */
    @Column(columnDefinition = "datetime(3)")
    private Timestamp birthday;
    /**
     * 用户手机号
     */
    @Column
    private String phone;
    /**
     * 用户邮箱
     */
    @Column
    private String email;
    /**
     * [PO_STATUS]用户状态:禁用,启用
     */
    @Column(columnDefinition = "enum ('INACTIVE', 'ACTIVE') default 'ACTIVE'")
    @Enumerated(EnumType.STRING)
    private UserStateEnum userState;

    @ManyToMany
    @JoinTable(name = "join_user_blog_group",
            // name:referencedColumnName 在中间表的别名  referencedColumnName:当前表关联字段名称
            joinColumns = {@JoinColumn(name = "userId", referencedColumnName = "userId")},
            // 关联关系另一方，其他属性同 joinColumns
            inverseJoinColumns = {@JoinColumn(name = "groupId", referencedColumnName = "groupId")}
    )
    private List<BlogGroup> blogGroupList;
}
