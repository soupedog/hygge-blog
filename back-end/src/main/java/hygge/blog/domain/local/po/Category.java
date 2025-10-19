package hygge.blog.domain.local.po;

import hygge.blog.common.HyggeRequestContext;
import hygge.blog.common.HyggeRequestTracker;
import hygge.blog.domain.local.enums.AccessRuleTypeEnum;
import hygge.blog.domain.local.enums.CategoryStateEnum;
import hygge.blog.domain.local.enums.CategoryTypeEnum;
import hygge.blog.domain.local.enums.UserSexEnum;
import hygge.blog.domain.local.po.base.BasePo;
import hygge.blog.domain.local.po.inner.CategoryAccessRule;
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
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.Optional;

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
     * 文章类别访问规则
     */
    @JdbcTypeCode(SqlTypes.JSON)
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

    public boolean accessibleForUser(User targetUser) {
        if (parameterHelper.isEmpty(accessRuleList)) {
            return false;
        }

        boolean result = false;
        HyggeRequestContext context = HyggeRequestTracker.getContext();
        String secretKey = context.getObject(HyggeRequestContext.Key.SECRET_KEY);

        if (targetUser == null) {
            // 访客
            if (parameterHelper.isNotEmpty(secretKey)) {
                Optional<CategoryAccessRule> secretKeyAccessRuleTemp = accessRuleList.stream().filter(categoryAccessRule -> AccessRuleTypeEnum.SECRET_KEY.equals(categoryAccessRule.getAccessRuleType())).findFirst();
                result = secretKey.equals(secretKeyAccessRuleTemp.map(CategoryAccessRule::getExtendString).orElse(null));
            }

            return result || accessRuleList.stream().anyMatch(categoryAccessRule -> AccessRuleTypeEnum.PUBLIC.equals(categoryAccessRule.getAccessRuleType()));
        }

        for (CategoryAccessRule categoryAccessRule : accessRuleList) {
            boolean itemResult = false;
            switch (categoryAccessRule.getAccessRuleType()) {
                case SECRET_KEY:
                    if (categoryAccessRule.getExtendString().equals(context.getObject(HyggeRequestContext.Key.SECRET_KEY))) {
                        itemResult = true;
                    }
                    break;
                case PERSONAL:
                    if (context.getCurrentLoginUser().getUserId().equals(targetUser.getUserId())) {
                        itemResult = true;
                    }
                    break;
                case GROUP:
                    List<BlogGroup> groupList = targetUser.getBlogGroupList();
                    if (groupList.stream().anyMatch(group -> group.getGid().equals(categoryAccessRule.getExtendString()))) {
                        itemResult = true;
                    }
                    break;
                case MALE:
                    if (targetUser.getUserSex().equals(UserSexEnum.MAN)) {
                        itemResult = true;
                    }
                    break;
                case FEMALE:
                    if (targetUser.getUserSex().equals(UserSexEnum.WOMAN)) {
                        itemResult = true;
                    }
                    break;
                case CRON:
                    // 暂时不会出现，先偷个懒
                    break;
                default:
                    itemResult = true;
            }

            if (categoryAccessRule.isRequirement()) {
                // 是必要条件则有一票否决权,必要条件一但不满足则返回无权限
                if (!itemResult) {
                    return false;
                } else {
                    result = true;
                }
            } else {
                result = result || itemResult;
            }
        }
        return result;
    }
}
