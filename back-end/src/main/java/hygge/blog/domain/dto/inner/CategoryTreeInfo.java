package hygge.blog.domain.dto.inner;

import hygge.blog.domain.dto.CategoryDto;
import hygge.blog.domain.dto.TopicDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * @author Xavier
 * @date 2022/7/24
 */
@Getter
@Setter
@Builder
@Generated
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "文章类别树信息", description = "描述目录层级关系")
public class CategoryTreeInfo {
    private TopicDto topicInfo;
    @Schema(title = "类别层级信息", description = "从父节点排列到当前类别节点")
    private List<CategoryDto> categoryList;
}
