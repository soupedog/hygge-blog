package hygge.blog.service;

import hygge.blog.common.HyggeRequestContext;
import hygge.blog.common.HyggeRequestTracker;
import hygge.blog.dao.CategoryDao;
import hygge.blog.domain.bo.BlogSystemCode;
import hygge.blog.domain.dto.CategoryDto;
import hygge.blog.domain.enums.AccessRuleTypeEnum;
import hygge.blog.domain.enums.CategoryTypeEnum;
import hygge.blog.domain.enums.UserTypeEnum;
import hygge.blog.domain.mapper.PoDtoMapper;
import hygge.blog.domain.po.Category;
import hygge.blog.domain.po.Topic;
import hygge.blog.domain.po.User;
import hygge.blog.domain.po.inner.CategoryAccessRule;
import hygge.commons.exceptions.LightRuntimeException;
import hygge.web.template.HyggeWebUtilContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Xavier
 * @date 2022/7/23
 */
@Service
public class CategoryServiceImpl extends HyggeWebUtilContainer {
    private static final CategoryAccessRule DEFAULT_CATEGORY_ACCESS_RULE = CategoryAccessRule.builder().accessRuleType(AccessRuleTypeEnum.PERSONAL).requirement(false).build();
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private TopicServiceImpl topicService;
    @Autowired
    private CategoryDao categoryDao;

    @Transactional
    public Category createCategory(CategoryDto categoryDto) {
        categoryDto.setCategoryType(CategoryTypeEnum.DEFAULT);
        parameterHelper.stringNotEmpty("categoryName", (Object) categoryDto.getCategoryName());
        categoryDto.setOrderVal(parameterHelper.integerFormatOfNullable("orderVal", categoryDto.getOrderVal(), 0));

        for (CategoryAccessRule accessRule : categoryDto.getAccessRuleList()) {
            parameterHelper.objectNotNull("accessRuleType", accessRule.getAccessRuleType());
            if (!(AccessRuleTypeEnum.PERSONAL.equals(accessRule.getAccessRuleType()) || AccessRuleTypeEnum.PUBLIC.equals(accessRule.getAccessRuleType()))) {
                parameterHelper.stringNotEmpty("extendString", (Object) accessRule.getExtendString());
            }
        }

        if (categoryDto.getAccessRuleList().isEmpty()) {
            categoryDto.getAccessRuleList().add(DEFAULT_CATEGORY_ACCESS_RULE);
        }

        HyggeRequestContext context = HyggeRequestTracker.getContext();
        User currentUser = context.getCurrentLoginUser();
        userService.checkUserRight(currentUser, UserTypeEnum.ROOT);

        Category category = PoDtoMapper.INSTANCE.dtoToPo(categoryDto);
        category.setCid(randomHelper.getUniversallyUniqueIdentifier(true));
        category.setUserId(currentUser.getUserId());

        Category old = categoryDao.findCategoryByCategoryName(category.getCategoryName());
        if (old != null) {
            throw new LightRuntimeException(String.format("Category(%s) already exists.", category.getCategoryName()), BlogSystemCode.ARTICLE_CATEGORY_ALREADY_EXISTS);
        }

        Topic topic = topicService.findTopicByTid(categoryDto.getTid(), false);
        category.setTopicId(topic.getTopicId());

        if (parameterHelper.isNotEmpty(categoryDto.getParentCid())) {
            Category parentCategory = findCategoryByCid(categoryDto.getParentCid(), false);
            category.setRootId(parentCategory.getRootId());
            category.setParentId(parentCategory.getCategoryId());
            category.setDepth(parentCategory.getDepth() + 1);
        } else {
            category.setDepth(0);
        }

        Category result = categoryDao.save(category);

        if (result.getDepth().equals(0)) {
            result.setRootId(result.getCategoryId());
            result = categoryDao.save(result);
        }
        return result;
    }

    public Category findCategoryByCid(String cid, boolean nullable) {
        Category result = categoryDao.findCategoryByCid(cid);
        return checkCategoryResult(result, cid, nullable);
    }

    private Category checkCategoryResult(Category categoryTemp, String info, boolean nullable) {
        if (!nullable && categoryTemp == null) {
            throw new LightRuntimeException(String.format("Category(%s) was not found.", info), BlogSystemCode.ARTICLE_CATEGORY_NOT_FOUND);
        }
        return categoryTemp;
    }
}
