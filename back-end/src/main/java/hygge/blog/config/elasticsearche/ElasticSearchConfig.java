package hygge.blog.config.elasticsearche;

import hygge.blog.config.elasticsearche.converter.DateToTimestamp;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchConverter;
import org.springframework.data.elasticsearch.core.convert.MappingElasticsearchConverter;
import org.springframework.data.elasticsearch.core.mapping.SimpleElasticsearchMappingContext;

/**
 * @author Xavier
 * @date 2022/8/29
 */
@Configuration
public class ElasticSearchConfig {
    @Bean
    public ElasticsearchConverter elasticsearchConverter(SimpleElasticsearchMappingContext mappingContext) {
        DefaultConversionService defaultConversionService = new DefaultConversionService();
        defaultConversionService.addConverter(new DateToTimestamp());
        return new MappingElasticsearchConverter(mappingContext, defaultConversionService);
    }
}
