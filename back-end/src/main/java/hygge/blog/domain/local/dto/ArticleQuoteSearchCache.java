package hygge.blog.domain.local.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@TypeAlias("ArticleQuoteSearchCache")// 默认是包路径，替换成恒定值更好
@Document(indexName = ArticleQuoteSearchCache.INDEX_NAME)
@Setting(shards = 1, replicas = 0)// 单机非集群 replicas 应设为 0(否则索引状态不会是 green)
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArticleQuoteSearchCache {
    public static final String INDEX_NAME = "article_quote_search_cache";
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
    @Field(type = FieldType.Text, analyzer = "smartcn")
    private String title;
    /**
     * 配图链接(共享)
     */
    @Field(type = FieldType.Text)
    private String imageSrc;
    /**
     * 文章摘要
     */
    @Field(type = FieldType.Text, analyzer = "smartcn")
    private String summary;
    /**
     * 内容(共享)
     */
    @Field(type = FieldType.Text, analyzer = "smartcn")
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
    @Field(type = FieldType.Text, analyzer = "smartcn")
    private String source;
    /**
     * 传送门
     */
    @Field(type = FieldType.Text)
    private String portal;
    /**
     * 句子备注
     */
    @Field(type = FieldType.Text, analyzer = "smartcn")
    private String remarks;

    public Integer initEsId(Integer idTemp, Type type) {
        if (Type.QUOTE.equals(type)) {
            esId = idTemp + INTERVAL;
        } else {
            esId = idTemp;
        }
        return esId;
    }

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