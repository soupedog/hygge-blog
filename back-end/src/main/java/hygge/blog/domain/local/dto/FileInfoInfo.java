package hygge.blog.domain.local.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 分页查询 FileInfo 的结果集合
 *
 * @author Xavier
 * @date 2025/10/28
 */
@Getter
@Setter
@Builder
@Generated
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "文件信息分页查询信息")
public class FileInfoInfo {
    @Schema(title = "文件信息数组")
    private List<FileInfoDto> fileInfoList;
    @Schema(title = "文件总数")
    private long totalCount;
}
