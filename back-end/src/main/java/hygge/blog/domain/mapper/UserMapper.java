package hygge.blog.domain.mapper;

import hygge.blog.domain.dto.UserDTO;
import hygge.blog.domain.mapper.convert.ObjectMappingConvert;
import hygge.blog.domain.po.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * @author Xavier
 * @date 2022/7/17
 */
@Mapper(imports = ObjectMappingConvert.class)
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mappings(value = {
            @Mapping(source = "uid", target = "uid", ignore = true),
            @Mapping(expression = "java(ObjectMappingConvert.longToTimestamp(userDTO.getBirthday()))", target = "birthday"),
    })
    User userDTOToUser(UserDTO userDTO);

    @Mappings(value = {
            @Mapping(source = "userName", target = "userName", ignore = true),
            @Mapping(source = "password", target = "password", ignore = true),
            @Mapping(expression = "java(ObjectMappingConvert.timestampToLong(user.getBirthday()))", target = "birthday"),
            @Mapping(source = "phone", target = "phone", ignore = true),
            @Mapping(source = "email", target = "email", ignore = true)
    })
    UserDTO userToUserDTO(User user);

}