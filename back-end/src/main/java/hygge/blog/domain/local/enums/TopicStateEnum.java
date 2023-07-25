package hygge.blog.domain.local.enums;

/**
 * [PO_STATUS]板块状态
 *
 * @author Xavier
 * @date 2022/7/17
 */
public enum TopicStateEnum {
    /**
     * 禁用
     */
    INACTIVE(0, "INACTIVE"),
    /**
     * 启用
     */
    ACTIVE(1, "ACTIVE");

    TopicStateEnum(Integer index, String value) {
        this.index = index;
        this.value = value;
    }

    public static TopicStateEnum parse(Integer index) {
        if (index == null) {
            throw new IllegalArgumentException("Unexpected index of TopicStateEnum,it can't be null.");
        }
        switch (index) {
            case 0:
                return TopicStateEnum.INACTIVE;
            case 1:
                return TopicStateEnum.ACTIVE;
            default:
                throw new IllegalArgumentException("Unexpected index of TopicStateEnum,it can't be " + index + ".");
        }
    }

    public static TopicStateEnum parse(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Unexpected value of TopicStateEnum,it can't be null.");
        }
        switch (value) {
            case "INACTIVE":
                return TopicStateEnum.INACTIVE;
            case "ACTIVE":
                return TopicStateEnum.ACTIVE;
            default:
                throw new IllegalArgumentException("Unexpected value of TopicStateEnum,it can't be " + value + ".");
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