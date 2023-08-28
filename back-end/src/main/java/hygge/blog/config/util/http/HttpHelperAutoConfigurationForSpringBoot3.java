package hygge.blog.config.util.http;

import hygge.commons.spring.config.configuration.definition.HyggeAutoConfiguration;
import hygge.web.config.HttpHelperAutoConfiguration;
import hygge.web.util.http.configuration.HttpHelperConfiguration;
import hygge.web.util.http.definition.HttpHelperLogger;
import hygge.web.util.http.definition.HttpHelperResponseEntityReader;
import hygge.web.util.http.definition.HttpHelperRestTemplateFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Xavier
 * @date 2023/8/28
 * @see HttpHelperAutoConfiguration
 */
@Slf4j
@Configuration
public class HttpHelperAutoConfigurationForSpringBoot3 implements HyggeAutoConfiguration {
    @Bean("defaultHttpHelperRestTemplateFactory")
    public HttpHelperRestTemplateFactory defaultHttpHelperRestTemplateFactory(HttpHelperConfiguration httpHelperConfiguration) {
        return new HttpHelperRestTemplateFactoryForSpringBoot3(httpHelperConfiguration);
    }

    @Bean("defaultHttpHelper")
    public HttpHelperForSpringBoot3 defaultHttpHelper(HttpHelperRestTemplateFactory httpHelperRestTemplateFactory, HttpHelperLogger httpHelperLogger, HttpHelperResponseEntityReader httpHelperResponseEntityReader) {
        log.info("HttpHelperForSpringBoot3 start to init.");
        return new HttpHelperForSpringBoot3(httpHelperRestTemplateFactory, httpHelperLogger, httpHelperResponseEntityReader);
    }
}
