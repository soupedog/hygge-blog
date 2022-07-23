package hygge.blog.domain.po;

import hygge.blog.domain.enums.CategoryStateEnum;
import hygge.blog.domain.enums.CategoryTypeEnum;
import hygge.blog.domain.po.base.BasePo;
import hygge.blog.domain.po.inner.CategoryAccessRule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import java.util.List;

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
     * 文章类别访问规则
     */
    @Type(type = "json")
    @Column(columnDefinition = "json")
    private List<CategoryAccessRule> accessRuleList;
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
