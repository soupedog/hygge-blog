package hygge.blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 入口程序
 *
 * @author Xavier
 * @date 2022/7/17
 */
@SpringBootApplication
public class HyggeBlogApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(HyggeBlogApplication.class);
        application.run(args);
    }
}
