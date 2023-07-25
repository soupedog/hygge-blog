package hygge.blog.domain.local.enums;

/**
 * [PO_STATUS]用户令牌 scope
 *
 * @author Xavier
 * @date 2022/7/17
 */
public enum TokenScopeEnum {
    /**
     * Web 端
     */
    WEB(0, "WEB"),
    /**
     * 移动端
     */
    PHONE(1, "PHONE");

    TokenScopeEnum(Integer index, String value) {
        this.index = index;
        this.value = value;
    }

    public static TokenScopeEnum parse(Integer index) {
        if (index == null) {
            throw new IllegalArgumentException("Unexpected index of TokenScopeEnum,it can't be null.");
        }
        switch (index) {
            case 0:
                return TokenScopeEnum.WEB;
            case 1:
                return TokenScopeEnum.PHONE;
            default:
                throw new IllegalArgumentException("Unexpected index of TokenScopeEnum,it can't be " + index + ".");
        }
    }

    public static TokenScopeEnum parse(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Unexpected value of TokenScopeEnum,it can't be null.");
        }
        switch (value) {
            case "WEB":
                return TokenScopeEnum.WEB;
            case "PHONE":
                return TokenScopeEnum.PHONE;
            default:
                throw new IllegalArgumentException("Unexpected value of TokenScopeEnum,it can't be " + value + ".");
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