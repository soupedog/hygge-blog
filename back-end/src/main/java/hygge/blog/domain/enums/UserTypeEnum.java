package hygge.blog.domain.enums;

/**
 * 用户身份
 *
 * @author Xavier
 * @date 2022/7/17
 */
public enum UserTypeEnum {
    /**
     * 管理员
     */
    ROOT(0, "ROOT"),
    /**
     * 普通用户
     */
    NORMAL(1, "NORMAL");

    UserTypeEnum(Integer index, String value) {
        this.index = index;
        this.value = value;
    }

    public static UserTypeEnum parse(Integer index) {
        if (index == null) {
            throw new IllegalArgumentException("Unexpected index of UserTypeEnum,it can't be null.");
        }
        switch (index) {
            case 0:
                return UserTypeEnum.ROOT;
            case 1:
                return UserTypeEnum.NORMAL;
            default:
                throw new IllegalArgumentException("Unexpected index of UserTypeEnum,it can't be " + index + ".");
        }
    }

    public static UserTypeEnum parse(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Unexpected value of UserTypeEnum,it can't be null.");
        }
        switch (value) {
            case "ROOT":
                return UserTypeEnum.ROOT;
            case "NORMAL":
                return UserTypeEnum.NORMAL;
            default:
                throw new IllegalArgumentException("Unexpected value of UserTypeEnum,it can't be " + value + ".");
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