package hygge.blog.domain.local.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
@Schema(title = "博客群组信息")
public class BlogGroupDto {
    @Schema(title = "群组编号", description = "系统自动生成群组编号")
    private String gid;
    @Schema(title = "群组名称", description = "群主自定义名称", example = "305")
    private String groupName;
    @Schema(title = "群主用户编号", description = "系统自动生成用户编号", example = "U00000001")
    private String uid;
}
