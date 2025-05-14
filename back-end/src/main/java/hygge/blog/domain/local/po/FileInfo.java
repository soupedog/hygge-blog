package hygge.blog.domain.local.po;

import hygge.blog.domain.local.po.base.BasePo;
import hygge.blog.domain.local.po.inner.FileDescription;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "fileInfo", indexes = {@Index(name = "index_fileNo", columnList = "fileNo", unique = true)})
public class FileInfo extends BasePo {
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
     * 文件名称(不包含扩展名)
     */
    private String name;
    /**
     * 文件扩展名(如 .png)
     */
    private String extension;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private FileDescription description;
    /**
     * 文件大小
     */
    private Long fileSize;
    @Lob
    @Column(columnDefinition = "longblob")
    private byte[] content;
}
