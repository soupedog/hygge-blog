package hygge.blog.domain.local.dto;

import hygge.blog.domain.local.dto.inner.CategoryTreeInfo;
import hygge.blog.domain.local.enums.ArticleStateEnum;
import hygge.blog.domain.local.po.inner.ArticleConfiguration;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
}
