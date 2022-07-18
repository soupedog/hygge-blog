package hygge.blog.domain.mapper.convert;

import hygge.blog.domain.enums.UserSexEnum;
import hygge.blog.domain.enums.UserStateEnum;
import hygge.blog.domain.enums.UserTypeEnum;
import hygge.utils.UtilsCreator;
import hygge.utils.definitions.ParameterHelper;

import java.sql.Timestamp;

/**
 * @author Xavier
 * @date 2022/7/18
 */
public class MapObjectConvert {
    private static final ParameterHelper parameterHelper = UtilsCreator.INSTANCE.getDefaultInstance(ParameterHelper.class);

    public String asString(Object target) {
        return parameterHelper.string(target);
    }

    public Integer asInteger(Object target) {
        return parameterHelper.integerFormat("target", target);
    }

    public Long asLong(Object target) {
        return parameterHelper.longFormat("target", target);
    }

    public Timestamp asTimestamp(Object target) {
        Long longValue = parameterHelper.longFormat("target", target);
        return longValue == null ? null : new Timestamp(longValue);
    }

    public UserSexEnum asUserSexEnum(Object target) {
        return UserSexEnum.parse(parameterHelper.string(target));
    }

    public UserTypeEnum asUserTypeEnum(Object target) {
        return UserTypeEnum.parse(parameterHelper.string(target));
    }

    public UserStateEnum asUserStateEnum(Object target) {
        return UserStateEnum.parse(parameterHelper.string(target));
    }
}
