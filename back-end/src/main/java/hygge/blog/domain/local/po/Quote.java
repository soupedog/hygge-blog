package hygge.blog.domain.local.po;

import hygge.blog.domain.local.enums.QuoteStateEnum;
import hygge.blog.domain.local.po.base.BasePo;
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
public class Quote extends BasePo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer quoteId;
    /**
     * 创建者唯一标识
     */
    @Column(nullable = false)
    private Integer userId;
    /**
     * 主图绝对路径
     */
    @Column(length = 1000)
    private String imageSrc;
    /**
     * 内容
     */
    @Column(length = 5000, unique = true)
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
    private QuoteStateEnum quoteState;
}
