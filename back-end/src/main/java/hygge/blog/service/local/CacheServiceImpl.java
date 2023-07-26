package hygge.blog.service.local;

import hygge.blog.common.mapper.PoDtoMapper;
import hygge.blog.domain.local.dto.CategoryDto;
import hygge.blog.domain.local.dto.inner.CategoryTreeInfo;
import hygge.blog.domain.local.po.Category;
import hygge.blog.domain.local.po.Topic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 对于复杂查询进行缓存的查询逻辑实现类
 * <p>
 * warn:该类不保证有完备入参合法性验证，请在调用该类之前进行必要的验证
 *
 * @author Xavier
 * @date 2023/7/26
 */
@Service
public class CacheServiceImpl {
    @Autowired
    private CategoryServiceImpl categoryService;
    @Autowired
    private TopicServiceImpl topicService;

    /**
     * 从传入的文章类型构造类别树一直到根节点
     */
    @Cacheable(cacheNames = "categoryTreeInfoCache", key = "'CategoryTree'+#categoryId", unless = "#result == null")
    public CategoryTreeInfo getCategoryTreeFormCurrent(Integer categoryId) {
        Category currentCategory = categoryService.findCategoryByCategoryId(categoryId, false);
        Topic topic = topicService.findTopicByTopicId(currentCategory.getTopicId(), false);

        List<CategoryDto> categoryList = new ArrayList<>();

        categoryList.add(PoDtoMapper.INSTANCE.poToDto(currentCategory));

        boolean continueFlag = true;
        Category currentNode = currentCategory;

        while (continueFlag) {
            Category parentNode = categoryService.findCategoryByCategoryId(currentNode.getParentId(), false);

            // 父节点是根节点了，可以停止
            if (parentNode.getRootId().equals(parentNode.getCategoryId())) {
                continueFlag = false;
            }

            categoryList.add(PoDtoMapper.INSTANCE.poToDto(parentNode));
            currentNode = parentNode;
        }

        // 当前是从子节点找到根节点，反转后才是从根到子节点的排列顺序
        Collections.reverse(categoryList);

        CategoryTreeInfo result = new CategoryTreeInfo();
        result.setTopicInfo(PoDtoMapper.INSTANCE.poToDto(topic));
        result.setCategoryList(categoryList);

        return result;
    }
}
