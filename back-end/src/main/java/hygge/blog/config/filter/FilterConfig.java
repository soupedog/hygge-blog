package hygge.blog.config.filter;

import hygge.blog.filter.HyggeRequestFilter;
import hygge.blog.filter.LoginFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

/**
 * filter 配置
 *
 * @author Xavier
 * @date 2022/7/20
 */
@Configuration
public class FilterConfig {
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
        filterRegistrationBean.addUrlPatterns("/blog-service/api/*");
        filterRegistrationBean.setOrder(Ordered.LOWEST_PRECEDENCE);
        return filterRegistrationBean;
    }
}
