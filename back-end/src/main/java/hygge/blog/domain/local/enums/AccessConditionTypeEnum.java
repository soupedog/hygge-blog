package hygge.blog.domain.local.enums;

/**
 * 访问规则类型
 *
 * @author Xavier
 * @date 2022/7/17
 */
public enum AccessConditionTypeEnum {
    /**
     * 仅自己可见(默认是 自己的 userId 添 - 号)
     */
    PERSONAL(-1, "PERSONAL"),
    /**
     * 公开可见
     */
    PUBLIC(0, "PUBLIC"),
    /**
     * 秘钥访问
     */
    SECRET_KEY(1, "SECRET_KEY"),
    /**
     * 特定群组成员可见
     */
    GROUP(2, "GROUP"),
    /**
     * 特定角色可见
     */
    ROLE(3, "ROLE"),
    /**
     * 特定性别可见
     */
    SEX(4, "SEX"),
    /**
     * 周期开放可见
     */
    CRON(5, "CRON"),
    ;

    AccessConditionTypeEnum(Integer index, String value) {
        this.index = index;
        this.value = value;
    }

    public static AccessConditionTypeEnum parse(Integer index) {
        if (index == null) {
            throw new IllegalArgumentException("Unexpected index of AccessConditionTypeEnum,it can't be null.");
        }

        if (index < 0) {
            return PERSONAL;
        }

        return switch (index) {
            case 0 -> PUBLIC;
            case 1 -> SECRET_KEY;
            case 2 -> GROUP;
            case 3 -> ROLE;
            case 4 -> SEX;
            case 5 -> CRON;
            default ->
                    throw new IllegalArgumentException("Unexpected index of AccessConditionTypeEnum,it can't be " + index + ".");
        };
    }

    public static AccessConditionTypeEnum parse(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Unexpected value of AccessConditionTypeEnum,it can't be null.");
        }
        return switch (value) {
            case "PERSONAL" -> PERSONAL;
            case "SECRET_KEY" -> SECRET_KEY;
            case "GROUP" -> GROUP;
            case "ROLE" -> ROLE;
            case "SEX" -> SEX;
            case "CRON" -> CRON;
            case "PUBLIC" -> PUBLIC;
            default ->
                    throw new IllegalArgumentException("Unexpected value of AccessConditionTypeEnum,it can't be " + value + ".");
        };
    }

    /**
     * 序号
     */
    private Integer index;
    /**
     * 枚举值
     */
    private String value;

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