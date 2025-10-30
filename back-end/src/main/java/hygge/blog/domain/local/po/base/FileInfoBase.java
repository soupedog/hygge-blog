package hygge.blog.domain.local.po.base;

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
import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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
     * 文章类别唯一标识展示用编号(与类别共享权限)
     */
    protected String cid;
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
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    protected FileDescription description;
    /**
     * 文件大小
     */
    protected Long fileSize;
}
