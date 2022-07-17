package hygge.blog.domain.enums;

/**
 * 媒体播放模式
 *
 * @author Xavier
 * @date 2022/7/17
 */
public enum MediaPlayTypeEnum {
    /**
     * 强制自动播放
     */
    FORCE_AUTO_PLAY(0, "FORCE_AUTO_PLAY"),
    /**
     * 强制非自动播放
     */
    FORCE_NOT_AUTO_PLAY(1, "FORCE_NOT_AUTO_PLAY"),
    /**
     * 建议自动播放(优先客户端本地配置)
     */
    SUGGEST_AUTO_PLAY(2, "SUGGEST_AUTO_PLAY"),
    /**
     * 建议非自动播放(优先客户端本地配置)
     */
    SUGGEST_NOT_AUTO_PLAY(3, "SUGGEST_NOT_AUTO_PLAY");

    MediaPlayTypeEnum(Integer index, String value) {
        this.index = index;
        this.value = value;
    }

    public static MediaPlayTypeEnum parse(Integer index) {
        if (index == null) {
            throw new IllegalArgumentException("Unexpected index of MediaPlayTypeEnum,it can't be null.");
        }
        switch (index) {
            case 0:
                return MediaPlayTypeEnum.FORCE_AUTO_PLAY;
            case 1:
                return MediaPlayTypeEnum.FORCE_NOT_AUTO_PLAY;
            case 2:
                return MediaPlayTypeEnum.SUGGEST_AUTO_PLAY;
            case 3:
                return MediaPlayTypeEnum.SUGGEST_NOT_AUTO_PLAY;
            default:
                throw new IllegalArgumentException("Unexpected index of MediaPlayTypeEnum,it can't be " + index + ".");
        }
    }

    public static MediaPlayTypeEnum parse(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Unexpected value of MediaPlayTypeEnum,it can't be null.");
        }
        switch (value) {
            case "FORCE_AUTO_PLAY":
                return MediaPlayTypeEnum.FORCE_AUTO_PLAY;
            case "FORCE_NOT_AUTO_PLAY":
                return MediaPlayTypeEnum.FORCE_NOT_AUTO_PLAY;
            case "SUGGEST_AUTO_PLAY":
                return MediaPlayTypeEnum.SUGGEST_AUTO_PLAY;
            case "SUGGEST_NOT_AUTO_PLAY":
                return MediaPlayTypeEnum.SUGGEST_NOT_AUTO_PLAY;
            default:
                throw new IllegalArgumentException("Unexpected value of MediaPlayTypeEnum,it can't be " + value + ".");
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