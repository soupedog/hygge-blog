package hygge.blog.domain.mapper;

import hygge.blog.domain.po.Topic;
import hygge.blog.domain.po.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

/**
 * @author Xavier
 * @date 2022/7/19
 */
@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OverrideMapper {
    OverrideMapper INSTANCE = Mappers.getMapper(OverrideMapper.class);

    void overrideToAnother(User one, @MappingTarget User another);
    void overrideToAnother(Topic one, @MappingTarget Topic another);
}
