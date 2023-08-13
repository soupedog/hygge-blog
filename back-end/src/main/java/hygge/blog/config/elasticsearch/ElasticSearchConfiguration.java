package hygge.blog.config.elasticsearch;

import hygge.commons.spring.config.configuration.definition.HyggeSpringConfigurationProperties;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * ElasticSearch 配置类
 *
 * @author Xavier
 * @date 2023/8/14
 */
@Getter
@Setter
@Generated
@NoArgsConstructor
@ConfigurationProperties(prefix = "hygge.blog.elastic-search")
public class ElasticSearchConfiguration implements HyggeSpringConfigurationProperties {
    /**
     * e.g:"localhost:9200"
     */
    private String hostAndPort;
}
