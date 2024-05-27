package hygge.blog.common.mapper.convert;

import hygge.util.template.HyggeJsonUtilContainer;

import java.sql.Timestamp;

/**
 * @author Xavier
 * @date 2022/7/17
 */
public class ObjectMappingConvert extends HyggeJsonUtilContainer {

    public static Timestamp longToTimestamp(Long target) {
        if (target == null) {
            return null;
        }
        return new Timestamp(parameterHelper.longFormatNotEmpty("TimeStamp(Long)", target));
    }

    public static Long timestampToLong(Timestamp target) {
        if (target == null) {
            return null;
        }
        return target.getTime();
    }
}
