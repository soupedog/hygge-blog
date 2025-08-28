package hygge.blog.service.local.normal;

import hygge.blog.common.HyggeRequestContext;
import hygge.blog.common.HyggeRequestTracker;
import hygge.blog.common.mapper.MapToAnyMapper;
import hygge.blog.common.mapper.OverrideMapper;
import hygge.blog.common.mapper.PoDtoMapper;
import hygge.blog.domain.local.bo.BlogSystemCode;
import hygge.blog.domain.local.dto.CategoryDto;
import hygge.blog.domain.local.enums.AccessRuleTypeEnum;
import hygge.blog.domain.local.enums.CategoryStateEnum;
import hygge.blog.domain.local.enums.CategoryTypeEnum;
import hygge.blog.domain.local.enums.UserTypeEnum;
import hygge.blog.domain.local.po.ArticleCountInfo;
import hygge.blog.domain.local.po.Category;
import hygge.blog.domain.local.po.Topic;
import hygge.blog.domain.local.po.User;
import hygge.blog.domain.local.po.inner.CategoryAccessRule;
import hygge.blog.repository.database.CategoryDao;
import hygge.commons.exception.LightRuntimeException;
import hygge.util.UtilCreator;
import hygge.util.bo.ColumnInfo;
import hygge.util.definition.DaoHelper;
import hygge.util.template.HyggeJsonUtilContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Xavier
 * @date 2022/7/23
 */
@Service
public class CategoryServiceImpl extends HyggeJsonUtilContainer {
    private static final DaoHelper daoHelper = UtilCreator.INSTANCE.getDefaultInstance(DaoHelper.class);
    private static final CategoryAccessRule DEFAULT_CATEGORY_ACCESS_RULE = CategoryAccessRule.builder().accessRuleType(AccessRuleTypeEnum.PERSONAL).requirement(false).build();

    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private ArticleCountServiceImpl articleCountService;
    @Autowired
    private TopicServiceImpl topicService;
    @Autowired
    private CategoryDao categoryDao;
    private static final Collection<ColumnInfo> forUpdate = new ArrayList<>();

    static {
        forUpdate.add(new ColumnInfo(true, false, "accessRuleList", null));
        forUpdate.add(new ColumnInfo(true, false, "categoryName", null).toStringColumn(1, 500));
        forUpdate.add(new ColumnInfo(true, false, "tid", null).toStringColumn(0, 500));
        forUpdate.add(new ColumnInfo(true, false, "uid", null).toStringColumn(0, 500));
        forUpdate.add(new ColumnInfo(true, false, "parentCid", null).toStringColumn(0, 500));
        forUpdate.add(new ColumnInfo(true, false, "orderVal", null, Integer.MIN_VALUE, Integer.MAX_VALUE));
        forUpdate.add(new ColumnInfo(true, false, "categoryState", null).toStringColumn(0, 50));
    }

    @Transactional
    public Category createCategory(CategoryDto categoryDto) {
        categoryDto.setCategoryType(CategoryTypeEnum.DEFAULT);
        parameterHelper.stringNotEmpty("categoryName", (Object) categoryDto.getCategoryName());
        categoryDto.setOrderVal(parameterHelper.integerFormatOfNullable("orderVal", categoryDto.getOrderVal(), 0));

        accessRuleListValidate(categoryDto.getAccessRuleList());

        if (categoryDto.getAccessRuleList().isEmpty()) {
            categoryDto.getAccessRuleList().add(DEFAULT_CATEGORY_ACCESS_RULE);
        }

        HyggeRequestContext context = HyggeRequestTracker.getContext();
        User currentUser = context.getCurrentLoginUser();
        userService.checkUserRight(currentUser, UserTypeEnum.ROOT);

        Category category = PoDtoMapper.INSTANCE.dtoToPo(categoryDto);
        category.setCid(randomHelper.getUniversallyUniqueIdentifier(true));
        category.setUserId(currentUser.getUserId());

        nameConflictCheck(category.getCategoryName());

        Topic topic = topicService.findTopicByTid(categoryDto.getTid(), false);
        category.setTopicId(topic.getTopicId());

        if (parameterHelper.isNotEmpty(categoryDto.getParentCid())) {
            Category parentCategory = findCategoryByCid(categoryDto.getParentCid(), false);
            ArticleCountInfo parentCategoryCountInfo = articleCountService.findArticleCountInfoOfCategory(parentCategory.getCategoryId(), currentUser.getUserId());
            if (parentCategory.getCategoryType().equals(CategoryTypeEnum.DEFAULT) && parentCategoryCountInfo != null && parentCategoryCountInfo.getCount() > 0) {
                throw new LightRuntimeException(String.format("Category(%s) can't contain article, but contains %d.", parentCategory.getCategoryName(), parentCategoryCountInfo.getCount()), BlogSystemCode.ARTICLE_CATEGORY_SUB_ARTICLE_NOT_EMPTY);
            }
            // 更新 parent Type
            if (parentCategory.getCategoryType().equals(CategoryTypeEnum.DEFAULT)) {
                parentCategory.setCategoryType(CategoryTypeEnum.PATH);
                categoryDao.save(parentCategory);
            }

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

    @Transactional
    public Category updateCategory(String cid, Map<String, Object> data) {
        parameterHelper.stringNotEmpty("cid", (Object) cid);

        HyggeRequestContext context = HyggeRequestTracker.getContext();
        User currentUser = context.getCurrentLoginUser();
        userService.checkUserRight(currentUser, UserTypeEnum.ROOT);

        Category old = findCategoryByCid(cid, false);

        HashMap<String, Object> finalData = daoHelper.filterOutTheFinalColumns(data, forUpdate);

        Category newOne = MapToAnyMapper.INSTANCE.mapToCategory(finalData);

        String categoryName = (String) finalData.get("categoryName");
        if (categoryName != null) {
            nameConflictCheck(categoryName);
        }
        String tid = (String) finalData.get("tid");
        if (tid != null) {
            Topic topic = topicService.findTopicByTid(tid, false);
            newOne.setTopicId(topic.getTopicId());
        }
        String uid = (String) finalData.get("uid");
        if (uid != null) {
            User user = userService.findUserByUid(uid, false);
            newOne.setUserId(user.getUserId());
        }
        String parentCid = (String) finalData.get("parentCid");
        if (parentCid != null) {
            Category parentCategory = findCategoryByCid(parentCid, false);
            ArticleCountInfo parentCategoryCountInfo = articleCountService.findArticleCountInfoOfCategory(parentCategory.getCategoryId(), currentUser.getUserId());
            if (parentCategory.getCategoryType().equals(CategoryTypeEnum.DEFAULT) && parentCategoryCountInfo.getCount() > 0) {
                throw new LightRuntimeException(String.format("Category(%s) can't contain article, but contains %d.", parentCategory.getCategoryName(), parentCategoryCountInfo.getCount()), BlogSystemCode.ARTICLE_CATEGORY_SUB_ARTICLE_NOT_EMPTY);
            }
            // 更新 parent Type
            if (parentCategory.getCategoryType().equals(CategoryTypeEnum.DEFAULT)) {
                parentCategory.setCategoryType(CategoryTypeEnum.PATH);
                categoryDao.save(parentCategory);
            }

            newOne.setParentId(parentCategory.getCategoryId());
            newOne.setRootId(parentCategory.getRootId());
            newOne.setDepth(parentCategory.getDepth() + 1);
        }

        if (newOne.getAccessRuleList() != null) {
            collectionHelper.collectionNotEmpty("accessRuleList", newOne.getAccessRuleList());
            accessRuleListValidate(newOne.getAccessRuleList());
        }

        OverrideMapper.INSTANCE.overrideToAnother(newOne, old);

        return categoryDao.save(old);
    }

    public List<Category> getAccessibleCategoryList(User currentUser, Collection<Integer> topicIdRequirement) {
        Example<Category> example = Example.of(Category.builder().categoryState(CategoryStateEnum.ACTIVE).build());
        List<Category> categoryList = categoryDao.findAll(example, Sort.by(Sort.Order.desc("orderVal")));

        List<Category> result = categoryList.stream().filter(category -> category.accessibleForUser(currentUser)).toList();
        if (parameterHelper.isNotEmpty(topicIdRequirement)) {
            result = result.stream().filter(category -> topicIdRequirement.contains(category.getTopicId())).toList();
        }
        return result;
    }

    private void nameConflictCheck(String categoryName) {
        Category old = categoryDao.findCategoryByCategoryName(categoryName);
        if (old != null) {
            throw new LightRuntimeException(String.format("Category(%s) already exists.", categoryName), BlogSystemCode.ARTICLE_CATEGORY_ALREADY_EXISTS);
        }
    }

    public Category findCategoryByCategoryId(Integer categoryId, boolean nullable) {
        Category result = categoryDao.findById(categoryId).orElse(null);
        return checkCategoryResult(result, parameterHelper.string(categoryId), nullable);
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

    private void accessRuleListValidate(List<CategoryAccessRule> accessRuleList) {
        for (CategoryAccessRule accessRule : accessRuleList) {
            parameterHelper.objectNotNull("accessRuleType", accessRule.getAccessRuleType());
            if (!(AccessRuleTypeEnum.PERSONAL.equals(accessRule.getAccessRuleType()) || AccessRuleTypeEnum.PUBLIC.equals(accessRule.getAccessRuleType()))) {
                parameterHelper.stringNotEmpty("extendString", (Object) accessRule.getExtendString());
            }
        }
    }
}
