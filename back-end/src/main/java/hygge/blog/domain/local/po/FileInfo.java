package hygge.blog.domain.local.po;

import hygge.blog.domain.local.po.base.FileInfoBase;
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
}
