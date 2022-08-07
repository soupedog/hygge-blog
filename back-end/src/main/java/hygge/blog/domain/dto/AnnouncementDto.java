package hygge.blog.domain.dto;

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
 * @date 2022/8/7
 */
@Getter
@Setter
@Builder
@Generated
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "公告信息")
public class AnnouncementDto {
    @Schema(title = "公告信息 Id")
    private Integer announcementId;
    @Schema(title = "公告内容")
    private List<String> paragraphList;
    @Schema(title = "标记颜色")
    private String color;
    @Schema(title = "标记符号")
    private String dot;
    @Schema(title = "创建时间")
    protected Long createTs;
}
