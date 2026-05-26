package hygge.blog.domain.local.po.base;

import hygge.blog.domain.local.dto.FileInfoDto;
import hygge.blog.domain.local.dto.inner.FileDescriptionDto;
import hygge.blog.domain.local.enums.FileCacheTypeEnum;
import hygge.blog.domain.local.enums.FileTypeEnum;
import hygge.blog.domain.local.po.inner.FileDescription;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.sql.Timestamp;
import java.util.Optional;

/**
 * @author Xavier
 * @date 2025/10/30
 */
@Getter
@Setter
@Generated
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@MappedSuperclass
public abstract class FileInfoBase extends BasePo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    protected Integer fileId;
    /**
     * 文件展示用唯一标识
     */
    protected String fileNo;
    /**
     * 上传者唯一标识
     */
    @Column(nullable = false)
    protected Integer userId;
    /**
     * 权限唯一标识
     */
    @Column(name = "permissionId")
    @ColumnDefault("-1")
    protected Integer permissionId;
    /**
     * 文件名称(不包含扩展名)
     */
    protected String name;
    /**
     * 文件扩展名(如 png)
     */
    protected String extension;
    /**
     * 文件类型
     */
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum ('CORE', 'QUOTE', 'ARTICLE_COVER', 'ARTICLE', 'BGM', 'OTHERS') default 'OTHERS'")
    protected FileTypeEnum fileType;
    /**
     * 文件缓存类型
     */
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum ('DEFAULT', 'NGINX') default 'DEFAULT'")
    protected FileCacheTypeEnum fileCacheType;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    protected FileDescription description;
    /**
     * 文件大小
     */
    protected Long fileSize;

    public FileInfoDto toDto() {
        FileInfoDto result = FileInfoDto.builder()
                .fileNo(fileNo)
                .permissionId(permissionId)
                .name(name)
                .extension(extension)
                .relativePath(returnRelativePath())
                .fileSize(unitConvertHelper.storageSmartFormatAsString(fileSize))
                .fileType(fileType)
                .fileCacheType(fileCacheType)
                .lastUpdateTs(lastUpdateTs.getTime())
                .createTs(createTs.getTime())
                .build();

        // FileDescription 对象可空，非空时才尝试初始化
        Optional.ofNullable(description).ifPresent((info) -> {
            if (parameterHelper.atLeastOneNotEmpty(info.getContent(), info.getTimePointer())) {
                // 至少有一个非空字段才初始化
                result.setDescription(
                        FileDescriptionDto.builder()
                                .content(info.getContent())
                                .timePointer(Optional.ofNullable(info.getTimePointer()).map(Timestamp::getTime).orElse(null))
                                .build()
                );
            }
        });
        return result;
    }

    public String returnRelativePath() {
        return fileType.getPath() + name + "." + extension;
    }
}
