package hygge.blog.domain.po;

import hygge.blog.domain.enums.UserSexEnum;
import hygge.blog.domain.enums.UserStateEnum;
import hygge.blog.domain.enums.UserTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import java.sql.Timestamp;

/**
 * @author Xavier
 * @date 2022/7/17
 */
@Entity
@Table(name = "user", indexes = {@Index(name = "index_uid", columnList = "uid", unique = true)})
@Getter
@Setter
@Builder
@Generated
@NoArgsConstructor
@AllArgsConstructor
public class User extends BasePO {
    /**
     * 唯一标识
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer userId;
    /**
     * [PO_PK_ALIAS]用户展示用唯一标识
     */
    @Column(length = 64)
    private String uid;
    /**
     * 用户身份
     */
    @Column(columnDefinition = "varchar(50) default 'NORMAL'")
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
    @Column(nullable = false)
    private String userName;
    /**
     * 用户头像链接
     */
    @Column(nullable = false)
    private String userAvatar;
    /**
     * 用户性别:保密,男,女
     */
    @Column(columnDefinition = "varchar(50) default 'SECRET'")
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
    @Column
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
    @Column(columnDefinition = "varchar(50) default 'ACTIVE'")
    @Enumerated(EnumType.STRING)
    private UserStateEnum userState;
}
