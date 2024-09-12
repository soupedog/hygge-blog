package hygge.blog.domain.local.dto;

import hygge.blog.domain.local.po.inner.FileDescription;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 文件信息
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
public class FileInfoDto {
    @Schema(title = "文件链接")
    private String src;
    @Schema(title = "文件编号")
    private String fileNo;
    @Schema(title = "用户编号")
    private String uid;
    @Schema(title = "文件名称(不包含扩展名)")
    private String name;
    @Schema(title = "文件扩展名(如 .png)")
    private String extension;
    private FileDescription description;
    @Schema(title = "文件大小")
    private String fileSize;
    @Schema(title = "文件本体")
    private byte[] content;
    @Schema(title = "创建时间", description = "UTC 毫秒级时间戳")
    protected Long createTs;
    @Schema(title = "最后修改时间", description = "UTC 毫秒级时间戳")
    protected Long lastUpdateTs;
}
