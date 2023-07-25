package hygge.blog.domain.local.enums;

/**
 * [PO_STATUS]文章状态
 *
 * @author Xavier
 * @date 2022/7/17
 */
public enum ArticleStateEnum {
    /**
     * 草稿
     */
    DRAFT(0, "DRAFT"),
    /**
     * 正常启用启用
     */
    ACTIVE(1, "ACTIVE"),
    /**
     * 私人的，仅自身可见
     */
    PRIVATE(2, "PRIVATE"),
    ;

    ArticleStateEnum(Integer index, String value) {
        this.index = index;
        this.value = value;
    }

    public static ArticleStateEnum parse(Integer index) {
        if (index == null) {
            throw new IllegalArgumentException("Unexpected index of ArticleStateEnum,it can't be null.");
        }
        switch (index) {
            case 0:
                return ArticleStateEnum.DRAFT;
            case 1:
                return ArticleStateEnum.ACTIVE;
            case 2:
                return ArticleStateEnum.PRIVATE;
            default:
                throw new IllegalArgumentException("Unexpected index of ArticleStateEnum,it can't be " + index + ".");
        }
    }

    public static ArticleStateEnum parse(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Unexpected value of ArticleStateEnum,it can't be null.");
        }
        switch (value) {
            case "DRAFT":
                return ArticleStateEnum.DRAFT;
            case "ACTIVE":
                return ArticleStateEnum.ACTIVE;
            case "PRIVATE":
                return ArticleStateEnum.PRIVATE;
            default:
                throw new IllegalArgumentException("Unexpected value of ArticleStateEnum,it can't be " + value + ".");
        }
    }

    /**
     * 序号
     */
    private Integer index;
    /**
     * 枚举值
     */
    private String value;

    /*非自动生成内容开始*/
    /*非自动生成内容结束*/

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}