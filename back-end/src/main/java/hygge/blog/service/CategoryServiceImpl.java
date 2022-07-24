package hygge.blog.service;

import com.fasterxml.jackson.core.type.TypeReference;
import hygge.blog.common.HyggeRequestContext;
import hygge.blog.common.HyggeRequestTracker;
import hygge.blog.dao.CategoryDao;
import hygge.blog.domain.bo.BlogSystemCode;
import hygge.blog.domain.dto.CategoryDto;
import hygge.blog.domain.enums.AccessRuleTypeEnum;
import hygge.blog.domain.enums.CategoryTypeEnum;
import hygge.blog.domain.enums.UserTypeEnum;
import hygge.blog.domain.mapper.MapToAnyMapper;
import hygge.blog.domain.mapper.OverrideMapper;
import hygge.blog.domain.mapper.PoDtoMapper;
import hygge.blog.domain.po.Category;
import hygge.blog.domain.po.Topic;
import hygge.blog.domain.po.User;
import hygge.blog.domain.po.inner.CategoryAccessRule;
import hygge.commons.enums.ColumnTypeEnum;
import hygge.commons.exceptions.LightRuntimeException;
import hygge.utils.UtilsCreator;
import hygge.utils.bo.ColumnInfo;
import hygge.utils.definitions.DaoHelper;
import hygge.web.template.HyggeWebUtilContainer;
import org.springframework.beans.factory.annotation.Autowired;
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
public class CategoryServiceImpl extends HyggeWebUtilContainer {
    private static final DaoHelper daoHelper = UtilsCreator.INSTANCE.getDefaultInstance(DaoHelper.class);
    private static final CategoryAccessRule DEFAULT_CATEGORY_ACCESS_RULE = CategoryAccessRule.builder().accessRuleType(AccessRuleTypeEnum.PERSONAL).requirement(false).build();

    private static TypeReference<ArrayList<CategoryAccessRule>> TYPE_INFO_ACCESS_RULE_LIST = new TypeReference<>() {
    };
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private TopicServiceImpl topicService;
    @Autowired
    private CategoryDao categoryDao;
    private static final Collection<ColumnInfo> forUpdate = new ArrayList<>();

    static {
        forUpdate.add(new ColumnInfo("categoryName", null, ColumnTypeEnum.STRING, true, false, 1, 500));
        forUpdate.add(new ColumnInfo("tid", null, ColumnTypeEnum.STRING, true, false, 0, 500));
        forUpdate.add(new ColumnInfo("uid", null, ColumnTypeEnum.STRING, true, false, 0, 500));
        forUpdate.add(new ColumnInfo("parentCid", null, ColumnTypeEnum.STRING, true, false, 0, 500));
        forUpdate.add(new ColumnInfo("orderVal", null, ColumnTypeEnum.INTEGER, true, false, 0, Integer.MAX_VALUE));
        forUpdate.add(new ColumnInfo("categoryState", null, ColumnTypeEnum.STRING, true, false, 0, 50));
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
            category.setRootId(parentCategory.getRootId());
            category.setParentId(parentCategory.getCategoryId());
            category.setDepth(parentCategory.getDepth() + 1);
            // TODO 更新 parent Type
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
            newOne.setParentId(parentCategory.getCategoryId());
            newOne.setRootId(parentCategory.getRootId());
            newOne.setDepth(parentCategory.getDepth() + 1);
            // TODO 更新 parent Type
        }

        Object accessRuleListTemp = data.get("accessRuleList");
        if (accessRuleListTemp != null) {
            ArrayList<CategoryAccessRule> accessRuleList = (ArrayList<CategoryAccessRule>) jsonHelper.readAsObjectWithClassInfo(jsonHelper.formatAsString(accessRuleListTemp), TYPE_INFO_ACCESS_RULE_LIST);
            accessRuleListValidate(accessRuleList);
            newOne.setAccessRuleList(accessRuleList);
        }

        OverrideMapper.INSTANCE.overrideToAnother(newOne, old);

        return categoryDao.save(old);
    }

    private void nameConflictCheck(String categoryName) {
        Category old = categoryDao.findCategoryByCategoryName(categoryName);
        if (old != null) {
            throw new LightRuntimeException(String.format("Category(%s) already exists.", categoryName), BlogSystemCode.ARTICLE_CATEGORY_ALREADY_EXISTS);
        }
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
