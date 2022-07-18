package hygge.blog.domain.mapper;

import hygge.blog.domain.mapper.convert.MapObjectConvert;
import hygge.blog.domain.po.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Map;

/**
 * @author Xavier
 * @date 2022/7/18
 */
@Mapper(uses = {MapObjectConvert.class})
public interface MapToAnyMapper {
    MapToAnyMapper INSTANCE = Mappers.getMapper(MapToAnyMapper.class);

    User mapToUser(Map<String, ?> map);
}