package hygge.blog.domain.local.enums;

/**
 * 站点浏览日志类型
 *
 * @author Xavier
 * @date 2025/12/22
 */
public enum BrowseLogTypeEnum {
    /**
     * 未进行检查的(默认)
     */
    UNCHECKED(0, "UNCHECKED"),
    /**
     * 默认(疑似真人)
     */
    DEFAULT(1, "DEFAULT"),
    /**
     * 百度机器人
     */
    BOT_BAIDU(2, "Baiduspider"),
    /**
     * 谷歌机器人
     */
    BOT_GOOGLE(3, "Googlebot"),
    /**
     * 360 机器人
     */
    BOT_360(4, "360Spider"),
    /**
     * 字节跳动机器人
     */
    BOT_Byte(5, "Bytespider"),
    /**
     * 机器人未知
     */
    BOT_UNKNOWN(6, "spider"),
    ;

    BrowseLogTypeEnum(Integer index, String value) {
        this.index = index;
        this.value = value;
    }

    public static BrowseLogTypeEnum parse(Integer index) {
        if (index == null) {
            throw new IllegalArgumentException("Unexpected index of BrowseLogTypeEnum,it can't be null.");
        }
        switch (index) {
            case 0:
                return BrowseLogTypeEnum.UNCHECKED;
            case 1:
                return BrowseLogTypeEnum.DEFAULT;
            case 2:
                return BrowseLogTypeEnum.BOT_BAIDU;
            case 3:
                return BrowseLogTypeEnum.BOT_GOOGLE;
            case 4:
                return BrowseLogTypeEnum.BOT_360;
            case 5:
                return BrowseLogTypeEnum.BOT_Byte;
            case 6:
                return BrowseLogTypeEnum.BOT_UNKNOWN;
            default:
                throw new IllegalArgumentException("Unexpected index of BrowseLogTypeEnum,it can't be " + index + ".");
        }
    }

    public static BrowseLogTypeEnum parse(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Unexpected value of BrowseLogTypeEnum,it can't be null.");
        }
        switch (value) {
            case "UNCHECKED":
                return BrowseLogTypeEnum.UNCHECKED;
            case "DEFAULT":
                return BrowseLogTypeEnum.DEFAULT;
            case "BOT_BAIDU":
                return BrowseLogTypeEnum.BOT_BAIDU;
            case "BOT_GOOGLE":
                return BrowseLogTypeEnum.BOT_GOOGLE;
            case "BOT_360":
                return BrowseLogTypeEnum.BOT_360;
            case "BOT_Byte":
                return BrowseLogTypeEnum.BOT_Byte;
            case "BOT_UNKNOWN":
                return BrowseLogTypeEnum.BOT_UNKNOWN;
            default:
                throw new IllegalArgumentException("Unexpected value of BrowseLogTypeEnum,it can't be " + value + ".");
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
