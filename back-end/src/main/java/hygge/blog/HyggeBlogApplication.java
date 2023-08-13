package hygge.blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 入口程序
 * "http://localhost:8080/swagger-ui/index.html"
 *
 * @author Xavier
 * @date 2022/7/17
 */
@EnableCaching
@EnableScheduling
@SpringBootApplication(scanBasePackages = "hygge.*")
public class HyggeBlogApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(HyggeBlogApplication.class);
        application.run(args);
    }
}
