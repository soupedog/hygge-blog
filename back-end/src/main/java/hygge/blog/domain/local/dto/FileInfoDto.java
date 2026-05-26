package hygge.blog.domain.local.dto;

import hygge.blog.domain.local.dto.inner.FileDescriptionDto;
import hygge.blog.domain.local.enums.FileCacheTypeEnum;
import hygge.blog.domain.local.enums.FileTypeEnum;
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
    @Schema(title = "文件编号")
    private String fileNo;
    @Schema(title = "文件相对路径")
    private String relativePath;
    @Schema(title = "用户编号")
    private String uid;
    @Schema(title = "权限唯一标识", description = "决定满足什么权限才运行访问。即便未主动创建特殊权限默认也存在：\"0\"->公开可见、\"null\"->仅自身可见")
    private Integer permissionId;
    @Schema(title = "文件名称(不包含扩展名)")
    private String name;
    @Schema(title = "文件扩展名(如 png)")
    private String extension;
    @Schema(title = "文件缓存类型")
    protected FileCacheTypeEnum fileCacheType;
    @Schema(title = "文件归档类别")
    private FileTypeEnum fileType;
    private FileDescriptionDto description;
    @Schema(title = "文件大小")
    private String fileSize;
    @Schema(title = "缓存链接")
    private String cacheLink;
    @Schema(title = "API 链接")
    private String apiLink;
    @Schema(title = "创建时间", description = "UTC 毫秒级时间戳")
    protected Long createTs;
    @Schema(title = "最后修改时间", description = "UTC 毫秒级时间戳")
    protected Long lastUpdateTs;
}
