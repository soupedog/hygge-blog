package hygge.blog.domain.mapper;

import hygge.blog.domain.dto.ArticleDto;
import hygge.blog.domain.dto.QuoteDto;
import hygge.blog.domain.mapper.convert.SimpleTypeConvert;
import hygge.blog.domain.dto.ArticleQuoteSearchCache;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * @author Xavier
 * @date 2022/8/29
 */
@Mapper(uses = SimpleTypeConvert.class)
public interface ElasticToDtoMapper {
    ElasticToDtoMapper INSTANCE = Mappers.getMapper(ElasticToDtoMapper.class);

    @Mapping(source = "articleState", target = "state")
    ArticleQuoteSearchCache articleDtoToEs(ArticleDto dto);

    @Mapping(source = "state", target = "articleState")
    ArticleDto esToArticleDto(ArticleQuoteSearchCache dto);

    @Mapping(source = "state", target = "quoteState")
    QuoteDto esToQuoteDto(ArticleQuoteSearchCache articleQuoteSearchCache);

    @Mapping(source = "quoteState", target = "state")
    ArticleQuoteSearchCache quoteDtoToEs(QuoteDto dto);
}
