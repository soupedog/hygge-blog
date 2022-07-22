package hygge.blog.domain.po;


import hygge.blog.domain.enums.TopicStateEnum;
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
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * 文章板块
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
@Table(name = "topic", indexes = {@Index(name = "index_tid", columnList = "tid", unique = true)})
public class Topic extends BasePO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer topicId;
    /**
     * [PO_PK_ALIAS]板块展示用编号
     */
    @Column(nullable = false)
    private String tid;
    /**
     * 板块名称
     */
    @Column(unique = true, nullable = false)
    private String topicName;
    /**
     * 板块拥有者唯一标识
     */
    @JoinColumn(nullable = false, name = "userId")
    @ManyToOne(targetEntity = User.class)
    private Integer userId;
    /**
     * 排序优先级(越大越靠前)
     */
    @Column
    private Integer orderVal;
    /**
     * [PO_STATUS]板块状态:禁用,启用
     */
    @Column(nullable = false, columnDefinition = "enum ('INACTIVE', 'ACTIVE') default 'ACTIVE'")
    @Enumerated(EnumType.STRING)
    private TopicStateEnum topicState;
}