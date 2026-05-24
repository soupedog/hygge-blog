package hygge.blog.domain.local.po;

import hygge.blog.domain.local.enums.CategoryStateEnum;
import hygge.blog.domain.local.enums.CategoryTypeEnum;
import hygge.blog.domain.local.po.base.BasePo;
import hygge.blog.service.local.normal.PermissionServiceImpl;
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
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

/**
 * 文章类别
 *
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
@Table(name = "category", indexes = {@Index(name = "index_cid", columnList = "cid", unique = true)})
@SuppressWarnings({"java:S3776"})
public class Category extends BasePo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer categoryId;
    /**
     * [PO_PK_ALIAS]文章类别唯一标识展示用编号
     */
    @Column(nullable = false)
    private String cid;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "enum ('DEFAULT', 'PATH') default 'DEFAULT'")
    private CategoryTypeEnum categoryType;
    /**
     * 权限唯一标识，应与 {@link PermissionServiceImpl#_PUBLIC#getPermissionId()} 一致
     */
    @Column(name = "permissionId")
    @ColumnDefault("-1")
    private Integer permissionId;
    /**
     * 文章类别名称
     */
    @Column(unique = true, nullable = false)
    private String categoryName;
    /**
     * 所属板块唯一标识
     */
    @Column(nullable = false)
    private Integer topicId;
    /**
     * 文章类别拥有者唯一标识
     */
    @Column(nullable = false)
    private Integer userId;

    @Column
    private Integer rootId;

    @Column
    private Integer parentId;

    @Column(nullable = false)
    private Integer depth;
    /**
     * 排序优先级(越大越靠前)
     */
    @Column(nullable = false)
    private Integer orderVal;
    /**
     * [PO_STATUS]文章类别状态:禁用,启用
     */
    @Column(nullable = false, columnDefinition = "enum ('INACTIVE', 'ACTIVE') default 'ACTIVE'")
    @Enumerated(EnumType.STRING)
    private CategoryStateEnum categoryState;
}
