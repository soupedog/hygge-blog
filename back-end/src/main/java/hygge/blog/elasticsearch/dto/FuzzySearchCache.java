package hygge.blog.elasticsearch.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.sql.Timestamp;

/**
 * 描述信息：<br/>
 *
 * @author Xavier
 * @version 1.0
 * @date 2020/9/4
 * @since Jdk 1.8
 */
@TypeAlias("article_quote")
@Document(indexName = "fuzzy_search_cache")
@Setting(shards = 1, replicas = 1)
@Getter
@Setter
public class FuzzySearchCache {
    /**
     * 文章、句子收藏间 ID 的间隔(防止 ES 文档覆盖)
     */
    public static final int INTERVAL = 1000000;
    /**
     * 内容唯一标示
     */
    @Id
    @Field(type = FieldType.Integer)
    private Integer esId;
    /**
     * 查询类型
     */
    @Field(type = FieldType.Keyword)
    private Type type;
    /**
     * 文章展示用编号
     */
    @Field(type = FieldType.Keyword)
    private String aid;
    @Field(type = FieldType.Text)
    private String configuration;
    @Field(type = FieldType.Text)
    private String categoryTreeInfo;
    /**
     * 文章类别编号
     */
    @Field(type = FieldType.Integer)
    private Integer categoryId;
    /**
     * 文章类型展示编号
     */
    @Field(type = FieldType.Keyword)
    private String cid;
    /**
     * 作者唯一标识
     */
    @Field(type = FieldType.Integer)
    private Integer userId;
    /**
     * 作者展示编号
     */
    @Field(type = FieldType.Keyword)
    private String uid;
    /**
     * 文章标题
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String title;
    /**
     * 配图链接(共享)
     */
    @Field(type = FieldType.Text)
    private String imageSrc;
    /**
     * 文章摘要
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String summary;
    /**
     * 内容(共享)
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String content;
    /**
     * 文章字数统计
     */
    @Field(type = FieldType.Integer)
    private Integer wordCount;
    /**
     * 浏览量
     */
    @Field(type = FieldType.Integer)
    private Integer pageViews;
    /**
     * 作者自身贡献的浏览量
     */
    @Field(type = FieldType.Integer)
    private Integer selfPageViews;
    @Field(type = FieldType.Keyword)
    private StateEnum state;
    /**
     * 创建时间 utc 毫秒级时间戳(共享)
     */
    @Field(type = FieldType.Date, format = {DateFormat.epoch_millis})
    private Timestamp createTs;
    /**
     * 最后修改时间 utc 毫秒级时间戳(共享)
     */
    @Field(type = FieldType.Date, format = {DateFormat.epoch_millis})
    private Timestamp lastUpdateTs;

    /**
     * 句子编号
     */
    @Field(type = FieldType.Integer)
    private Integer quoteId;
    /**
     * 句子可能出处
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String source;
    /**
     * 传送门
     */
    @Field(type = FieldType.Text)
    private String portal;
    /**
     * 句子备注
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String remarks;

    public enum StateEnum {
        /**
         * 草稿
         */
        DRAFT,
        /**
         * 正常启用启用
         */
        ACTIVE,
        /**
         * 私人的，仅自身可见
         */
        PRIVATE,
        /**
         * 禁用的
         */
        INACTIVE
    }

    public enum Type {
        ARTICLE, QUOTE
    }
}