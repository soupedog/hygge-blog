package hygge.blog.common.annotation;

import hygge.blog.domain.local.enums.UserTypeEnum;
import hygge.blog.filter.LoginFilter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Filter 联动注解，在 Controller 层方法上使用，以便提前做权限校验，尽可能早地拒绝无效请求
 *
 * @author Xavier
 * @date 2026/5/4
 * @see LoginFilter
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireAuth {
    /**
     * 仅当当前用户属于下列身份才运行放行
     */
    UserTypeEnum[] userType() default {UserTypeEnum.ROOT};
}
