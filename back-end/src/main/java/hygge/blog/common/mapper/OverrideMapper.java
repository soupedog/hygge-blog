package hygge.blog.common.mapper;

import hygge.blog.domain.local.po.Article;
import hygge.blog.domain.local.po.Category;
import hygge.blog.domain.local.po.Quote;
import hygge.blog.domain.local.po.Topic;
import hygge.blog.domain.local.po.User;
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

    void overrideToAnother(Category one, @MappingTarget Category another);
    void overrideToAnother(Article one, @MappingTarget Article another);
    void overrideToAnother(Quote one, @MappingTarget Quote another);
}
