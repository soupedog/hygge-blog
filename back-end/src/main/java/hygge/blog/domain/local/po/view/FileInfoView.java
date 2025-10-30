package hygge.blog.domain.local.po.view;

import hygge.blog.domain.local.dto.FileInfoDto;
import hygge.blog.domain.local.dto.inner.FileDescriptionDto;
import hygge.blog.domain.local.po.FileInfo;
import hygge.blog.domain.local.po.base.FileInfoBase;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.sql.Timestamp;
import java.util.Optional;

/**
 * @author Xavier
 * @date 2025/10/28
 */
@Getter
@Setter
@Generated
@SuperBuilder
@NoArgsConstructor
@Entity
@Table(name = "fileInfo_glance_view")
public class FileInfoView extends FileInfoBase {
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
                        .timePointer(Optional.ofNullable(info.getTimePointer()).map(Timestamp::getTime).orElse(null))
                        .build()
        ));
        return result;
    }
}
