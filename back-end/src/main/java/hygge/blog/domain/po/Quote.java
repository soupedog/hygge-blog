package hygge.blog.domain.po;

import hygge.blog.domain.enums.QuoteStateEnum;
import hygge.blog.domain.po.base.BasePO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * 句子收藏
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
@Table(name = "quote")
public class Quote extends BasePO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer quoteId;
    /**
     * 创建者唯一标识
     */
    @ManyToOne(targetEntity = User.class)
    @JoinColumn(nullable = false, name = "userId")
    private Integer userId;
    /**
     * 主图绝对路径
     */
    @Column
    private String imageSrc;
    /**
     * 内容
     */
    @Column(length = 5000)
    private String content;
    /**
     * 可能的出处
     */
    @Column(length = 2000)
    private String source;
    /**
     * 传送门
     */
    @Column(length = 2000)
    private String portal;
    /**
     * 备注
     */
    @Column(length = 5000)
    private String remarks;
    /**
     * 排序优先级(越大越靠前)
     */
    @Column
    private Integer orderVal;
    /**
     * [PO_STATUS]句子收藏状态:禁用,启用
     */
    @Column(columnDefinition = "enum ('INACTIVE', 'ACTIVE') default 'ACTIVE'")
    @Enumerated(EnumType.STRING)
    private QuoteStateEnum sentenceState;
}
