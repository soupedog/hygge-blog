package hygge.blog.domain.local.enums;

/**
 * 背景音乐类型
 *
 * @author Xavier
 * @date 2022/7/17
 */
public enum BackgroundMusicTypeEnum {
    /**
     * 无背景音乐
     */
    NONE(0, "NONE"),
    /**
     * 默认类型(绝对路径)
     */
    DEFAULT(1, "DEFAULT"),
    /**
     * 网易云外链
     */
    WANG_YI_YUN(2, "WANG_YI_YUN");

    BackgroundMusicTypeEnum(Integer index, String value) {
        this.index = index;
        this.value = value;
    }

    public static BackgroundMusicTypeEnum parse(Integer index) {
        if (index == null) {
            throw new IllegalArgumentException("Unexpected index of BackgroundMusicTypeEnum,it can't be null.");
        }
        switch (index) {
            case 0:
                return BackgroundMusicTypeEnum.NONE;
            case 1:
                return BackgroundMusicTypeEnum.DEFAULT;
            case 2:
                return BackgroundMusicTypeEnum.WANG_YI_YUN;
            default:
                throw new IllegalArgumentException("Unexpected index of BackgroundMusicTypeEnum,it can't be " + index + ".");
        }
    }

    public static BackgroundMusicTypeEnum parse(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Unexpected value of BackgroundMusicTypeEnum,it can't be null.");
        }
        switch (value) {
            case "NONE":
                return BackgroundMusicTypeEnum.NONE;
            case "DEFAULT":
                return BackgroundMusicTypeEnum.DEFAULT;
            case "WANG_YI_YUN":
                return BackgroundMusicTypeEnum.WANG_YI_YUN;
            default:
                throw new IllegalArgumentException("Unexpected value of BackgroundMusicTypeEnum,it can't be " + value + ".");
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