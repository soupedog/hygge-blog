package hygge.blog.config.elasticsearch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * @author Xavier
 * @date 2023/8/11
 */
@Configuration
@EnableConfigurationProperties(value = ElasticSearchConfiguration.class)
@EnableElasticsearchRepositories(
        basePackages = "hygge.blog.repository.elasticsearch"
)
public class ElasticSearchConfig extends ElasticsearchConfiguration {
    @Autowired
    private ElasticSearchConfiguration elasticSearchConfiguration;

    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo(elasticSearchConfiguration.getHostAndPort())
                .build();
    }
}
