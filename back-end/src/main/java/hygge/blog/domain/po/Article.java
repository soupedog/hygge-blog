package hygge.blog.domain.po;

import hygge.blog.domain.enums.ArticleStateEnum;
import hygge.blog.domain.po.base.BasePo;
import hygge.blog.domain.po.inner.ArticleConfiguration;
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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

/**
 * 文章
 *
 * @author Xavier
 * @date 2022/7/21
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
@Table(name = "article", indexes = {@Index(name = "index_aid", columnList = "aid", unique = true)})
public class Article extends BasePo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer articleId;
    /**
     * [PO_PK_ALIAS]文章展示用唯一标识
     */
    @Column(nullable = false)
    private String aid;
    /**
     * 文章配置项
     */
    @Type(type = "json")
    @Column(columnDefinition = "json")
    private ArticleConfiguration configuration;
    /**
     * 文章类别唯一标识
     */
    @Column(nullable = false)
    private Integer categoryId;
    /**
     * 作者唯一标识
     */
    @Column(nullable = false)
    private Integer userId;
    /**
     * 文章标题
     */
    @Column(nullable = false, unique = true, length = 500)
    private String title;
    /**
     * 文章配图链接
     */
    @Column(nullable = false, length = 1000)
    private String imageSrc;
    /**
     * 文章摘要
     */
    @Column(length = 3000)
    private String summary;
    /**
     * 文章内容
     */
    @Column(columnDefinition = "text")
    private String content;
    /**
     * 文章字数
     */
    @Column
    private Integer wordCount;
    /**
     * 总浏览量
     */
    @Column
    private Integer pageViews;
    /**
     * 作者自身贡献的浏览量
     */
    @Column
    private Integer selfPageViews;
    /**
     * 全局排序优先级(越大越靠前)
     */
    @Column
    private Integer orderGlobal;
    /**
     * 类别内排序优先级(越大越靠前)
     */
    @Column
    private Integer orderCategory;
    /**
     * [PO_STATUS]文章状态:禁用,启用
     */
    @Column(nullable = false, columnDefinition = "enum ('INACTIVE', 'ACTIVE') default 'ACTIVE'")
    @Enumerated(EnumType.STRING)
    private ArticleStateEnum articleState;
}
