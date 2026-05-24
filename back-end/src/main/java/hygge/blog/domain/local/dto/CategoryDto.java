package hygge.blog.domain.local.dto;

import hygge.blog.domain.local.enums.CategoryStateEnum;
import hygge.blog.domain.local.enums.CategoryTypeEnum;
import hygge.blog.domain.local.po.inner.CategoryAccessRule;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 文章类别
 *
 * @author Xavier
 * @date 2022/7/23
 */
@Getter
@Setter
@Builder
@Generated
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {
    @Schema(title = "板块编号", description = "系统自动生成板块编号")
    private String tid;
    @Schema(title = "文章类别编号", description = "系统自动生成文章类别编号")
    private String cid;
    @Schema(title = "权限唯一标识", description = "决定满足什么权限才运行访问。即便未主动创建特殊权限默认也存在：\"0\"->公开可见、\"null\"->仅自身可见")
    private Integer permissionId;
    @Schema(title = "文章类别类型", description = "PATH 类型下不允许挂载文章")
    private CategoryTypeEnum categoryType;
    @Schema(title = "文章类别名称", description = "创建者自定义的文章类别名称")
    private String categoryName;
    @Schema(title = "用户编号", description = "系统自动生成用户编号", example = "U00000001")
    private String uid;
    @Schema(title = "文章类别父节点编号", description = "系统自动生成文章类别编号")
    private String parentCid;
    @Schema(title = "排序优先级", description = "越大越靠前")
    private Integer orderVal;
    @Schema(title = "文章类别状态", description = "禁用,启用")
    private CategoryStateEnum categoryState;
    @Schema(title = "文章类别下文章数量")
    private Integer articleCount;
}
