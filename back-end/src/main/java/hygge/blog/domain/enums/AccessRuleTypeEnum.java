package hygge.blog.domain.enums;

/**
 * [PO_STATUS]访问规则类型
 *
 * @author Xavier
 * @date 2022/7/17
 */
public enum AccessRuleTypeEnum {
    /**
     * 仅自己可见
     */
    PERSONAL(0, "PERSONAL"),
    /**
     * 秘钥访问
     */
    SECRET_KEY(1, "SECRET_KEY"),
    /**
     * 群组
     */
    GROUP(2, "GROUP"),
    /**
     * 男性可见
     */
    MALE(3, "MALE"),
    /**
     * 女性可见
     */
    FEMALE(4, "FEMALE"),
    /**
     * 周期开放
     */
    CRON(5, "CRON"),
    /**
     * 公开可见
     */
    PUBLIC(6, "PUBLIC");

    AccessRuleTypeEnum(Integer index, String value) {
        this.index = index;
        this.value = value;
    }

    public static AccessRuleTypeEnum parse(Integer index) {
        if (index == null) {
            throw new IllegalArgumentException("Unexpected index of AccessRuleTypeEnum,it can't be null.");
        }
        switch (index) {
            case 0:
                return AccessRuleTypeEnum.PERSONAL;
            case 1:
                return AccessRuleTypeEnum.SECRET_KEY;
            case 2:
                return AccessRuleTypeEnum.GROUP;
            case 3:
                return AccessRuleTypeEnum.MALE;
            case 4:
                return AccessRuleTypeEnum.FEMALE;
            case 5:
                return AccessRuleTypeEnum.CRON;
            case 6:
                return AccessRuleTypeEnum.PUBLIC;
            default:
                throw new IllegalArgumentException("Unexpected index of AccessRuleTypeEnum,it can't be " + index + ".");
        }
    }

    public static AccessRuleTypeEnum parse(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Unexpected value of AccessRuleTypeEnum,it can't be null.");
        }
        switch (value) {
            case "PERSONAL":
                return AccessRuleTypeEnum.PERSONAL;
            case "SECRET_KEY":
                return AccessRuleTypeEnum.SECRET_KEY;
            case "GROUP":
                return AccessRuleTypeEnum.GROUP;
            case "MALE":
                return AccessRuleTypeEnum.MALE;
            case "FEMALE":
                return AccessRuleTypeEnum.FEMALE;
            case "CRON":
                return AccessRuleTypeEnum.CRON;
            case "PUBLIC":
                return AccessRuleTypeEnum.PUBLIC;
            default:
                throw new IllegalArgumentException("Unexpected value of AccessRuleTypeEnum,it can't be " + value + ".");
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