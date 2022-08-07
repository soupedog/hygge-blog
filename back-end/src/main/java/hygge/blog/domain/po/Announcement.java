package hygge.blog.domain.po;

import hygge.blog.domain.po.base.BasePo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.List;

/**
 * @author Xavier
 * @date 2022/8/7
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
@Table(name = "announcement")
public class Announcement extends BasePo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer announcementId;

    @Type(type = "json")
    @Column(columnDefinition = "json")
    private List<String> paragraphList;

    @Column(columnDefinition = "varchar(100) default 'blue'")
    private String color;

    private String dot;
}
