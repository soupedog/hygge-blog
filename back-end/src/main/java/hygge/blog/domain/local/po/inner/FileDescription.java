package hygge.blog.domain.local.po.inner;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

/**
 * 文件描述信息
 *
 * @author Xavier
 * @date 2024/9/12
 * @since 1.0
 */
@Getter
@Setter
@Builder
@Generated
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "文件描述信息")
public class FileDescription {
    @Schema(title = "文件描述文本信息")
    private String content;
    @Schema(title = "时间指向(描述文件的诞生时间等)")
    private Timestamp timePointer;
}
