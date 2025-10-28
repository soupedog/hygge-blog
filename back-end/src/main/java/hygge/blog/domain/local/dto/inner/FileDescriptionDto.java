package hygge.blog.domain.local.dto.inner;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * @author Xavier
 * @date 2025/10/28
 */
@Getter
@Setter
@Builder
@Generated
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "文件描述信息")
public class FileDescriptionDto {
    @Schema(title = "文件描述文本信息")
    private String content;
    @Schema(title = "时间指向(描述文件的诞生时间等)")
    private Long timePointer;
}
