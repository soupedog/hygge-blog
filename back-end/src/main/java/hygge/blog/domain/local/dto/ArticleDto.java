package hygge.blog.domain.local.dto;

import hygge.blog.domain.local.dto.inner.CategoryTreeInfo;
import hygge.blog.domain.local.enums.ArticleStateEnum;
import hygge.blog.common.mapper.PoDtoMapper;
import hygge.blog.domain.local.po.Category;
import hygge.blog.domain.local.po.inner.ArticleConfiguration;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 文章
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
@Schema(title = "文章信息")
public class ArticleDto {
    @Schema(title = "文章编号", description = "系统自动生成文章编号")
    private String aid;
    private ArticleConfiguration configuration;
    private CategoryTreeInfo categoryTreeInfo;
    @Schema(title = "文章类别编号", description = "系统自动生成文章类别编号")
    private String cid;
    @Schema(title = "用户编号", description = "系统自动生成用户编号", example = "U00000001")
    private String uid;
    @Schema(title = "文章标题")
    private String title;
    @Schema(title = "文章配图链接")
    private String imageSrc;
    @Schema(title = "文章摘要")
    private String summary;
    @Schema(title = "文章内容")
    private String content;
    @Schema(title = "文章字数")
    private Integer wordCount;
    @Schema(title = "总浏览量")
    private Integer pageViews;
    @Schema(title = "作者自身贡献的浏览量")
    private Integer selfPageViews;
    @Schema(title = "全局排序优先级", description = "越大越靠前")
    private Integer orderGlobal;
    @Schema(title = "类别内排序优先级", description = "越大越靠前")
    private Integer orderCategory;
    @Schema(title = "文章状态", description = "草稿,启用,私人的")
    private ArticleStateEnum articleState;
    @Schema(title = "创建时间", description = "UTC 毫秒级时间戳")
    protected Long createTs;
    @Schema(title = "最后修改时间", description = "UTC 毫秒级时间戳")
    protected Long lastUpdateTs;

    public void initCategoryTreeInfo(TopicDto currentTopicDto, Category currentCategory, List<Category> allCategoryList) {
        CategoryTreeInfo categoryTreeInfo = new CategoryTreeInfo();
        categoryTreeInfo.setTopicInfo(currentTopicDto);
        categoryTreeInfo.setCategoryList(new ArrayList<>(0));

        // 确保当前节点一定被添加
        while (categoryTreeInfo.getCategoryList().isEmpty() || currentCategory != null) {
            categoryTreeInfo.getCategoryList().add(PoDtoMapper.INSTANCE.poToDto(currentCategory));
            // 确认不会空指针
            Integer parentId = currentCategory.getParentId();
            if (currentCategory.getParentId() != null) {
                currentCategory = allCategoryList.stream().filter(item -> item.getCategoryId().equals(parentId)).findFirst().orElse(null);
            } else {
                currentCategory = null;
            }
        }

        // 上面是从当前找到根节点，所以需要反转数组才是从根到当前节点
        Collections.reverse(categoryTreeInfo.getCategoryList());
        this.setCategoryTreeInfo(categoryTreeInfo);
    }
}
