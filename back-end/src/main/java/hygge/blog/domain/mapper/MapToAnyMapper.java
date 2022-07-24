package hygge.blog.domain.mapper;

import hygge.blog.domain.mapper.convert.MapObjectConvert;
import hygge.blog.domain.po.Category;
import hygge.blog.domain.po.Topic;
import hygge.blog.domain.po.User;
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

    @Mapping(source = "accessRuleList", target = "accessRuleList", ignore = true)
    Category mapToCategory(Map<String, ?> map);
}