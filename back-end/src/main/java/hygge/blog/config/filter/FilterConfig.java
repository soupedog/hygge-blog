package hygge.blog.config.filter;

import hygge.blog.domain.local.enums.UserTypeEnum;
import hygge.blog.filter.HyggeRequestFilter;
import hygge.blog.filter.LoginFilter;
import hygge.blog.filter.UserRoleFilter;
import hygge.util.template.HyggeJsonUtilContainer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static hygge.blog.filter.UserRoleFilter.userRoleMapping;

/**
 * filter 配置
 *
 * @author Xavier
 * @date 2022/7/20
 */
@Slf4j
@Configuration
public class FilterConfig extends HyggeJsonUtilContainer {
    @Bean
    public FilterRegistrationBean<HyggeRequestFilter> hyggeRequestFilterRegistration(HyggeRequestFilter hyggeRequestFilter) {
        FilterRegistrationBean<HyggeRequestFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(hyggeRequestFilter);
        filterRegistrationBean.addUrlPatterns("/blog-service/api/*");
        filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return filterRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean<LoginFilter> loginFilterRegistration(LoginFilter loginFilter) {
        FilterRegistrationBean<LoginFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(loginFilter);
        // 非登录接口都进行 token 校验
        filterRegistrationBean.addUrlPatterns("/blog-service/api/main/*");
        filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 10);
        return filterRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean<UserRoleFilter> userRoleFilterRegistration(UserRoleFilter userRoleFilter) {
        FilterRegistrationBean<UserRoleFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(userRoleFilter);

        MultiValueMap<UserTypeEnum, String> multiValueMap = new LinkedMultiValueMap();

        userRoleMapping.forEach((path, userTypeEnum) -> {
            // 配置过权限要求的接口都进行校验
            filterRegistrationBean.addUrlPatterns(path);
            multiValueMap.add(userTypeEnum, path);
        });
        String mappingInfo = jsonHelper.formatAsString(multiValueMap);
        log.info("UserRoleFilter role mapping:" + mappingInfo);
        filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 11);
        return filterRegistrationBean;
    }
}
