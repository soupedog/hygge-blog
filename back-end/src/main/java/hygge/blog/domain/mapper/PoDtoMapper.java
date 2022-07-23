package hygge.blog.domain.mapper;

import hygge.blog.domain.dto.BlogGroupDto;
import hygge.blog.domain.dto.TopicDto;
import hygge.blog.domain.dto.UserDto;
import hygge.blog.domain.dto.UserTokenDto;
import hygge.blog.domain.mapper.convert.ObjectMappingConvert;
import hygge.blog.domain.po.BlogGroup;
import hygge.blog.domain.po.Topic;
import hygge.blog.domain.po.User;
import hygge.blog.domain.po.UserToken;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * @author Xavier
 * @date 2022/7/17
 */
@Mapper(imports = ObjectMappingConvert.class)
public interface PoDtoMapper {
    PoDtoMapper INSTANCE = Mappers.getMapper(PoDtoMapper.class);

    @Mappings(value = {
            @Mapping(source = "uid", target = "uid", ignore = true),
            @Mapping(expression = "java(ObjectMappingConvert.longToTimestamp(dto.getBirthday()))", target = "birthday"),
    })
    User dtoToPo(UserDto dto);

    @Mappings(value = {
            @Mapping(source = "userName", target = "userName", ignore = true),
            @Mapping(source = "password", target = "password", ignore = true),
            @Mapping(expression = "java(ObjectMappingConvert.timestampToLong(po.getBirthday()))", target = "birthday"),
            @Mapping(source = "phone", target = "phone", ignore = true),
            @Mapping(source = "email", target = "email", ignore = true)
    })
    UserDto poToDto(User po);

    @Mappings(value = {
            @Mapping(expression = "java(ObjectMappingConvert.timestampToLong(po.getDeadline()))", target = "deadline")
    })
    UserTokenDto poToDto(UserToken po);

    BlogGroupDto poToDto(BlogGroup po);

    BlogGroup dtoToPo(BlogGroupDto dto);

    TopicDto poToDto(Topic po);

    Topic dtoToPo(TopicDto dto);
}