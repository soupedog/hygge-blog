package hygge.blog.domain.local.po.view;

import hygge.blog.domain.local.po.base.FileInfoBase;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

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
// Hibernate 会自动建表对视图无法正确适配，总会尝试自动创建(需要手工确定是创建好了视图)
@Table(name = "fileInfo_glance_view")
public class FileInfoView extends FileInfoBase {
}
