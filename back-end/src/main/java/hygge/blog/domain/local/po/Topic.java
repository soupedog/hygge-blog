package hygge.blog.domain.local.po;

import hygge.blog.domain.local.enums.TopicStateEnum;
import hygge.blog.domain.local.po.base.BasePo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

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
public class Topic extends BasePo {
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
    @Column(nullable = false)
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