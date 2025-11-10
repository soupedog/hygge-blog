package hygge.blog.domain.local.enums;

/**
 * 文件副本类型
 *
 * @author Xavier
 * @date 2025/11/10
 */
public enum FileCopyTypeEnum {
    /**
     * 默认(即没有数据库外的副本)
     */
    DEFAULT(0, "DEFAULT"),
    /**
     * Nginx 服务器上存在副本
     */
    NGINX(1, "NGINX");

    FileCopyTypeEnum(Integer index, String value) {
        this.index = index;
        this.value = value;
    }

    public static FileCopyTypeEnum parse(Integer index) {
        if (index == null) {
            throw new IllegalArgumentException("Unexpected index of FileCopyTypeEnum,it can't be null.");
        }
        switch (index) {
            case 0:
                return FileCopyTypeEnum.DEFAULT;
            case 1:
                return FileCopyTypeEnum.NGINX;
            default:
                throw new IllegalArgumentException("Unexpected index of FileCopyTypeEnum,it can't be " + index + ".");
        }
    }

    public static FileCopyTypeEnum parse(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Unexpected value of FileCopyTypeEnum,it can't be null.");
        }
        switch (value) {
            case "DEFAULT":
                return FileCopyTypeEnum.DEFAULT;
            case "NGINX":
                return FileCopyTypeEnum.NGINX;
            default:
                throw new IllegalArgumentException("Unexpected value of FileCopyTypeEnum,it can't be " + value + ".");
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
