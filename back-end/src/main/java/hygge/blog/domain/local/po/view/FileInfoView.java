package hygge.blog.domain.local.po.view;

import hygge.blog.domain.local.dto.FileInfoDto;
import hygge.blog.domain.local.dto.inner.FileDescriptionDto;
import hygge.blog.domain.local.enums.FileTypeEnum;
import hygge.blog.domain.local.po.FileInfo;
import hygge.blog.domain.local.po.base.BasePo;
import hygge.blog.domain.local.po.inner.FileDescription;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Optional;

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
@Entity
@Table(name = "fileInfo_glance_view")
public class FileInfoView extends BasePo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer fileId;
    /**
     * 文件展示用唯一标识
     */
    private String fileNo;
    /**
     * 上传者唯一标识
     */
    @Column(nullable = false)
    private Integer userId;
    /**
     * 文章类别唯一标识展示用编号(与类别共享权限)
     */
    private String cid;
    /**
     * 文件名称(不包含扩展名)
     */
    private String name;
    /**
     * 文件扩展名(如 .png)
     */
    private String extension;
    /**
     * 文件类型
     */
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum ('CORE', 'QUOTE', 'ARTICLE_COVER', 'ARTICLE', 'BGM', 'OTHERS') default 'OTHERS'")
    private FileTypeEnum fileType;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private FileDescription description;
    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 请确保与 {@link FileInfo#toDto()} 逻辑一致
     */
    public FileInfoDto toDto() {
        FileInfoDto result = FileInfoDto.builder()
                .fileNo(fileNo)
                .name(name)
                .extension(extension)
                .src(fileType.getPath() + name + "." + extension)
                .fileSize(unitConvertHelper.storageSmartFormatAsString(getFileSize()))
                .fileType(fileType)
                .lastUpdateTs(lastUpdateTs.getTime())
                .createTs(createTs.getTime())
                .build();

        // FileDescription 对象可空，非空时才初始化
        Optional.ofNullable(description).ifPresent((info) -> result.setDescription(
                FileDescriptionDto.builder()
                        .content(info.getContent())
                        .timePointer(info.getTimePointer().getTime())
                        .build()
        ));
        return result;
    }
}
