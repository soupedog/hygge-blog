package hygge.blog.service.local;

import hygge.blog.common.mapper.PoDtoMapper;
import hygge.blog.domain.local.dto.CategoryDto;
import hygge.blog.domain.local.dto.inner.CategoryTreeInfo;
import hygge.blog.domain.local.enums.FileCacheTypeEnum;
import hygge.blog.domain.local.po.Category;
import hygge.blog.domain.local.po.Topic;
import hygge.blog.domain.local.po.User;
import hygge.blog.domain.local.po.view.FileInfoView;
import hygge.blog.service.local.inner.file.FileUrlBuilder;
import hygge.blog.service.local.normal.CategoryServiceImpl;
import hygge.blog.service.local.normal.TopicServiceImpl;
import hygge.blog.service.local.normal.UserServiceImpl;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
    private final CategoryServiceImpl categoryService;
    private final TopicServiceImpl topicService;
    private final UserServiceImpl userService;
    private final FileServiceImpl fileService;
    private final FileUrlBuilder fileUrlBuilder;

    public CacheServiceImpl(CategoryServiceImpl categoryService, TopicServiceImpl topicService, UserServiceImpl userService, FileServiceImpl fileService, FileUrlBuilder fileUrlBuilder) {
        this.categoryService = categoryService;
        this.topicService = topicService;
        this.userService = userService;
        this.fileService = fileService;
        this.fileUrlBuilder = fileUrlBuilder;
    }

    /**
     * 从传入的文章类型构造类别树一直到根节点
     */
    @Cacheable(cacheNames = "categoryTreeInfoCache", key = "'CategoryTree'+#categoryId", unless = "#result == null")
    public CategoryTreeInfo getCategoryTreeFormCurrent(Integer categoryId) {
        Category currentCategory = categoryService.findCategoryByCategoryId(categoryId, false);
        Topic topic = topicService.findTopicByTopicId(currentCategory.getTopicId(), false);

        List<CategoryDto> categoryList = new ArrayList<>();

        categoryList.add(PoDtoMapper.INSTANCE.poToDto(currentCategory));

        Category currentNode = currentCategory;

        // 已经是根节点类型的话直接结束，否则继续
        boolean continueFlag = !currentNode.getRootId().equals(currentNode.getCategoryId());

        while (continueFlag) {
            Category parentNode = categoryService.findCategoryByCategoryId(currentNode.getParentId(), false);
            // 如果父节点是根节点了，应停止寻根
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

    /**
     * 根据 userId 获取对应的 uid
     */
    @Cacheable(cacheNames = "userIdToUidMappingCache", key = "'UserIdToUid'+#userId", unless = "#result == null")
    public String userIdToUid(Integer userId) {
        if (userId == null) {
            return null;
        }
        User user = userService.findUserByUserId(userId, true);
        return user == null ? null : user.getUid();
    }

    /**
     * 根据 fileNo 获取对应的 file 外部访问链接
     */
    @Cacheable(cacheNames = "fileNoToFileUrlMappingCache", key = "'fileNoToFileUrl'+#fileNo", unless = "#result == null")
    public String fileNoToFileUrl(String fileNo) {
        if (fileNo == null) {
            return null;
        }

        Optional<FileInfoView> fileInfoViewTemp = fileService.findFileViewFromDB(fileNo);

        if (fileInfoViewTemp.isEmpty()) {
            return null;
        }

        FileInfoView fileInfoView = fileInfoViewTemp.get();

        if (fileInfoView.getFileCacheType().equals(FileCacheTypeEnum.DEFAULT)) {
            return fileUrlBuilder.getFileApiLink(fileNo);
        }

        return null;
    }
}
