package hygge.blog.common.mapper;

import hygge.blog.domain.local.dto.AnnouncementDto;
import hygge.blog.domain.local.dto.ArticleDto;
import hygge.blog.domain.local.dto.BlogGroupDto;
import hygge.blog.domain.local.dto.CategoryDto;
import hygge.blog.domain.local.dto.QuoteDto;
import hygge.blog.domain.local.dto.TopicDto;
import hygge.blog.domain.local.dto.UserDto;
import hygge.blog.domain.local.dto.UserTokenDto;
import hygge.blog.common.mapper.convert.ObjectMappingConvert;
import hygge.blog.domain.local.po.Announcement;
import hygge.blog.domain.local.po.Article;
import hygge.blog.domain.local.po.BlogGroup;
import hygge.blog.domain.local.po.Category;
import hygge.blog.domain.local.po.Quote;
import hygge.blog.domain.local.po.Topic;
import hygge.blog.domain.local.po.User;
import hygge.blog.domain.local.po.UserToken;
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

    @Mapping(source = "topicState", target = "topicState", ignore = true)
    TopicDto poToDto(Topic po);

    Topic dtoToPo(TopicDto dto);

    @Mapping(source = "accessRuleList", target = "accessRuleList", ignore = true)
    @Mapping(source = "categoryState", target = "categoryState", ignore = true)
    CategoryDto poToDto(Category po);

    Category dtoToPo(CategoryDto dto);

    @Mapping(expression = "java(ObjectMappingConvert.timestampToLong(po.getCreateTs()))", target = "createTs")
    @Mapping(expression = "java(ObjectMappingConvert.timestampToLong(po.getLastUpdateTs()))", target = "lastUpdateTs")
    ArticleDto poToDto(Article po);

    Article dtoToPo(ArticleDto dto);

    QuoteDto poToDto(Quote po);

    Quote dtoToPo(QuoteDto dto);

    @Mapping(expression = "java(ObjectMappingConvert.timestampToLong(po.getCreateTs()))", target = "createTs")
    AnnouncementDto poToDto(Announcement po);

    Announcement dtoToPo(AnnouncementDto dto);
}