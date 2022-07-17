package hygge.blog.domain.bo;

import hygge.commons.exceptions.code.HyggeCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 错误码列表
 *
 * @author Xavier
 * @date 2022/7/17
 */
@Getter
public enum BlogSystemCode implements HyggeCode<Integer, HttpStatus> {
    /**
     * 用户登录信息有误
     */
    LOGIN_ILLEGAL(false, "Unexpected login info.", 403000, null),
    /**
     * 当前用户权限不足
     */
    INSUFFICIENT_PERMISSIONS(false, "Insufficient permissions.", 403001, null),
    /**
     * 登录账号或密码有误
     */
    LOGIN_FAIL(false, "AC was not found,or unexpected password.", 404000, null),
    /**
     * 登录创建 UserToken 失败
     */
    LOGIN_CREATE_CONFLICT(true, "Login conflict.", 409000, null),
    /**
     * 用户对象未找到
     */
    USER_NOT_FOUND(false, null, 404201, null),
    /**
     * 板块未找到
     */
    BOARD_NOT_FOUND(false, null, 404301, null),
    /**
     * 文章类别下挂载文章不为空
     */
    ARTICLE_CATEGORY_SUB_ARTICLE_NOT_EMPTY(false, null, 403401, null),
    /**
     * 文章类别节点深度不符合预期
     */
    ARTICLE_CATEGORY_UNEXPECTED_DEPTH(false, null, 403402, null),
    /**
     * 文章类别名称已存在
     */
    ARTICLE_CATEGORY_ALREADY_EXISTS(false, null, 403403, null),
    /**
     * 文章类别子节点不为空
     */
    ARTICLE_CATEGORY_SUB_NODE_NOT_EMPTY(false, null, 403404, null),
    /**
     * 文章类别下文章不为空
     */
    ARTICLE_CATEGORY_ARTICLE_NOT_EMPTY(false, null, 403405, null),
    /**
     * 文章类型未找到
     */
    ARTICLE_CATEGORY_NOT_FOUND(false, null, 404401, null),
    /**
     * 文章类别树信息未找到
     */
    ARTICLE_CATEGORY_TREE_INFO_NOT_FOUND(false, null, 404402, null),
    /**
     * 文章类别落库失败
     */
    ARTICLE_CATEGORY_SAVE_CONFLICT(false, null, 409401, null),
    /**
     * 文章类别树信息插入落库失败
     */
    ARTICLE_CATEGORY_TREE_INFO_SAVE_CONFLICT(false, null, 409402, null),
    /**
     * 文章类别树信息更新落库失败
     */
    ARTICLE_CATEGORY_TREE_INFO_UPDATE_CONFLICT(false, null, 409403, null),
    /**
     * 文章类别访问规则插入落库失败
     */
    ARTICLE_CATEGORY_ACCESS_RULE_SAVE_CONFLICT(false, null, 409404, null),
    /**
     * 用户名称已存在
     */
    USER_ALREADY_EXISTS(false, null, 403201, null),
    /**
     * 文章名称已存在
     */
    ARTICLE_ALREADY_EXISTS(false, null, 403501, null),
    /**
     * 文章不存在
     */
    ARTICLE_NOT_FOUND(false, null, 404501, null),
    /**
     * 文章配置项不存在
     */
    ARTICLE_CONFIGURATION_NOT_FOUND(false, null, 404502, null),
    /**
     * 文章插入落库失败
     */
    ARTICLE_SAVE_CONFLICT(false, null, 409501, null),
    /**
     * 文章更新落库失败
     */
    ARTICLE_UPDATE_CONFLICT(false, null, 409502, null),
    /**
     * 句子收藏落库失败
     */
    SENTENCE_COLLECTION_SAVE_CONFLICT(false, null, 409601, null),
    /**
     * 句子收藏更新落库失败
     */
    SENTENCE_COLLECTION_UPDATE_CONFLICT(false, null, 409602, null),
    /**
     * 构造文章类型树形结构失败
     */
    FAIL_TO_CREATE_ARTICLE_CATEGORY_TREE(true, null, 500001, null),
    /**
     * 反序列化 Json 失败
     */
    FAIL_TO_PRES_JSON(true, null, 500002, null),
    /**
     * 上传文件失败
     */
    FAIL_TO_UPLOAD_FILE(true, null, 500003, null),
    /**
     * 查询文件失败
     */
    FAIL_TO_QUERY_FILE(true, null, 500004, null),
    /**
     * 删除文件失败
     */
    FAIL_TO_DELETE_FILE(true, null, 500004, null),
    ;

    private final boolean serious;
    private final String publicMessage;
    private final Integer code;
    private final HttpStatus extraInfo;

    BlogSystemCode(boolean serious, String publicMessage, int code, HttpStatus extraInfo) {
        this.serious = serious;
        this.publicMessage = publicMessage;
        this.code = code;
        this.extraInfo = extraInfo;
    }

    @Override
    public boolean serious() {
        return serious;
    }

    @Override
    public String getPublicMessage() {
        return publicMessage;
    }

    @Override
    public <Co> Co getCode() {
        return (Co) code;
    }

    @Override
    public <Ex> Ex getExtraInfo() {
        return (Ex) extraInfo;
    }
}