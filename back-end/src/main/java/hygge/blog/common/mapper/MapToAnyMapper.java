package hygge.blog.common.mapper;

import hygge.blog.common.mapper.convert.MapObjectConvert;
import hygge.blog.domain.local.po.Article;
import hygge.blog.domain.local.po.Category;
import hygge.blog.domain.local.po.Quote;
import hygge.blog.domain.local.po.Topic;
import hygge.blog.domain.local.po.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Map;

/**
 * @author Xavier
 * @date 2022/7/18
 */
@Mapper(uses = {MapObjectConvert.class})
public interface MapToAnyMapper {
    MapToAnyMapper INSTANCE = Mappers.getMapper(MapToAnyMapper.class);

    @Mapping(source = "blogGroupList", target = "blogGroupList", ignore = true)
    User mapToUser(Map<String, ?> map);

    @Mapping(source = "topicId", target = "topicId", ignore = true)
    @Mapping(source = "tid", target = "tid", ignore = true)
    Topic mapToTopic(Map<String, ?> map);

    Category mapToCategory(Map<String, ?> map);

    Article mapToArticle(Map<String, ?> map);

    Quote mapToQuote(Map<String, ?> map);
}