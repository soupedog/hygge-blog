package hygge.blog.config.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Xavier
 * @date 2022/7/17
 */
@Configuration
public class SpringDocConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(new Info()
                        .title("Hygge Blog 博客服务 API 文档")
                        .version("2022.7")
                        .description("该 Swagger 用于方便在本地环境进行一系列操作，部署环境默认是关闭的")
                );
    }
}
