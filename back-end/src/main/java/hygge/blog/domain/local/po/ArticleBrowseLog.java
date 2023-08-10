package hygge.blog.domain.local.po;

import hygge.blog.domain.local.po.base.BasePo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Xavier
 * @date 2022/11/19
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
@Table(name = "article_browse_log")
public class ArticleBrowseLog extends BasePo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ablId;
    private String title;
    private String ip;
    private String ipLocation;
    private String aid;
    /**
     * 纬度
     */
    private String latitude;
    /**
     * 经度
     */
    private String longitude;
    private Integer userId;
    private String userAgent;
}
