package hygge.blog.domain.enums;

/**
 * 用户性别
 *
 * @author Xavier
 * @date 2022/7/17
 */
public enum UserSexEnum {
    /**
     * 保密
     */
    SECRET(0, "SECRET"),
    /**
     * 男
     */
    MAN(1, "MAN"),
    /**
     * 女
     */
    WOMAN(2, "WOMAN");

    UserSexEnum(Integer index, String value) {
        this.index = index;
        this.value = value;
    }

    public static UserSexEnum parse(Integer index) {
        if (index == null) {
            throw new IllegalArgumentException("Unexpected index of UserSexEnum,it can't be null.");
        }
        switch (index) {
            case 0:
                return UserSexEnum.SECRET;
            case 1:
                return UserSexEnum.MAN;
            case 2:
                return UserSexEnum.WOMAN;
            default:
                throw new IllegalArgumentException("Unexpected index of UserSexEnum,it can't be " + index + ".");
        }
    }

    public static UserSexEnum parse(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Unexpected value of UserSexEnum,it can't be null.");
        }
        switch (value) {
            case "SECRET":
                return UserSexEnum.SECRET;
            case "MAN":
                return UserSexEnum.MAN;
            case "WOMAN":
                return UserSexEnum.WOMAN;
            default:
                throw new IllegalArgumentException("Unexpected value of UserSexEnum,it can't be " + value + ".");
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