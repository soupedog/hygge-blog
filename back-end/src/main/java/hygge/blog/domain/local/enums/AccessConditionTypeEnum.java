package hygge.blog.domain.local.enums;

/**
 * 访问规则类型
 *
 * @author Xavier
 * @date 2022/7/17
 */
public enum AccessConditionTypeEnum {
    /**
     * 仅自己可见
     */
    PERSONAL(0, "PERSONAL"),
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
    /**
     * 公开可见
     */
    PUBLIC(6, "PUBLIC");

    AccessConditionTypeEnum(Integer index, String value) {
        this.index = index;
        this.value = value;
    }

    public static AccessConditionTypeEnum parse(Integer index) {
        if (index == null) {
            throw new IllegalArgumentException("Unexpected index of AccessConditionTypeEnum,it can't be null.");
        }
        switch (index) {
            case 0:
                return AccessConditionTypeEnum.PERSONAL;
            case 1:
                return AccessConditionTypeEnum.SECRET_KEY;
            case 2:
                return AccessConditionTypeEnum.GROUP;
            case 3:
                return AccessConditionTypeEnum.ROLE;
            case 4:
                return AccessConditionTypeEnum.SEX;
            case 5:
                return AccessConditionTypeEnum.CRON;
            case 6:
                return AccessConditionTypeEnum.PUBLIC;
            default:
                throw new IllegalArgumentException("Unexpected index of AccessConditionTypeEnum,it can't be " + index + ".");
        }
    }

    public static AccessConditionTypeEnum parse(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Unexpected value of AccessConditionTypeEnum,it can't be null.");
        }
        switch (value) {
            case "PERSONAL":
                return AccessConditionTypeEnum.PERSONAL;
            case "SECRET_KEY":
                return AccessConditionTypeEnum.SECRET_KEY;
            case "GROUP":
                return AccessConditionTypeEnum.GROUP;
            case "ROLE":
                return AccessConditionTypeEnum.ROLE;
            case "SEX":
                return AccessConditionTypeEnum.SEX;
            case "CRON":
                return AccessConditionTypeEnum.CRON;
            case "PUBLIC":
                return AccessConditionTypeEnum.PUBLIC;
            default:
                throw new IllegalArgumentException("Unexpected value of AccessConditionTypeEnum,it can't be " + value + ".");
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