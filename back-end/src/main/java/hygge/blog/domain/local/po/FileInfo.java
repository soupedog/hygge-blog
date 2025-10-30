package hygge.blog.domain.local.po;

import hygge.blog.domain.local.dto.FileInfoDto;
import hygge.blog.domain.local.dto.inner.FileDescriptionDto;
import hygge.blog.domain.local.po.base.FileInfoBase;
import hygge.blog.domain.local.po.view.FileInfoView;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.sql.Timestamp;
import java.util.Optional;

/**
 * 文件信息
 *
 * @author Xavier
 * @date 2024/9/12
 * @since 1.0
 */
@Getter
@Setter
@Generated
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "fileInfo", indexes = {@Index(name = "index_fileNo", columnList = "fileNo", unique = true)})
public class FileInfo extends FileInfoBase {
    @Lob
    @Column(columnDefinition = "longblob", updatable = false)
    private byte[] content;

    /**
     * 请确保与 {@link FileInfoView#toDto()} 逻辑一致
     */
    public FileInfoDto toDto() {
        FileInfoDto result = FileInfoDto.builder()
                .fileNo(fileNo)
                .name(name)
                .extension(extension)
                .src(fileType.getPath() + name + "." + extension)
                .fileSize(unitConvertHelper.storageSmartFormatAsString(fileSize))
                .fileType(fileType)
                .lastUpdateTs(lastUpdateTs.getTime())
                .createTs(createTs.getTime())
                .build();

        // FileDescription 对象可空，非空时才初始化
        Optional.ofNullable(description).ifPresent((info) -> result.setDescription(
                FileDescriptionDto.builder()
                        .content(info.getContent())
                        .timePointer(Optional.ofNullable(info.getTimePointer()).map(Timestamp::getTime).orElse(null))
                        .build()
        ));
        return result;
    }
}
