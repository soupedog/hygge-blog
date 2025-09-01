package hygge.blog.service.local.normal;

import hygge.blog.common.HyggeRequestContext;
import hygge.blog.common.HyggeRequestTracker;
import hygge.blog.common.mapper.PoDtoMapper;
import hygge.blog.domain.local.dto.AnnouncementDto;
import hygge.blog.domain.local.dto.CategoryDto;
import hygge.blog.domain.local.dto.HomepageFetchResult;
import hygge.blog.domain.local.dto.QuoteInfo;
import hygge.blog.domain.local.dto.TopicDto;
import hygge.blog.domain.local.dto.inner.ArticleSummaryInfo;
import hygge.blog.domain.local.dto.inner.TopicOverviewInfo;
import hygge.blog.domain.local.po.Announcement;
import hygge.blog.domain.local.po.ArticleCountInfo;
import hygge.blog.domain.local.po.Category;
import hygge.blog.domain.local.po.Topic;
import hygge.blog.domain.local.po.User;
import hygge.util.template.HyggeJsonUtilContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Xavier
 * @date 2022/7/25
 */
@Service
public class HomePageServiceImpl extends HyggeJsonUtilContainer {
    @Autowired
    private TopicServiceImpl topicService;
    @Autowired
    private CategoryServiceImpl categoryService;
    @Autowired
    private ArticleServiceImpl articleService;
    @Autowired
    private ArticleCountServiceImpl articleCountService;
    @Autowired
    private QuoteServiceImpl quoteService;
    @Autowired
    private AnnouncementServiceImpl announcementService;

    /**
     * 如果页容量不为空，将拉取首个主题下的文章摘要,以它为页容量进行分页查询第一页(如果主题存在的话)
     *
     * @param pageSize 页容量
     */
    public HomepageFetchResult fetch(Integer pageSize) {
        HyggeRequestContext context = HyggeRequestTracker.getContext();
        User currentUser = context.getCurrentLoginUser();

        List<Topic> topicList = topicService.findAllTopic();

        List<Category> categoryList = categoryService.getAccessibleCategoryList(currentUser, null);

        List<Integer> accessibleCategoryIdList = collectionHelper.filterNonemptyItemAsArrayList(false, categoryList, Category::getCategoryId);

        List<ArticleCountInfo> articleCountInfoList = articleCountService.findArticleCountInfoOfCategory(accessibleCategoryIdList, context.isGuest() ? null : currentUser.getUserId());

        HomepageFetchResult result = HomepageFetchResult.builder().topicOverviewInfoList(new ArrayList<>()).build();

        // 记录首个 Topic 对象
        Topic firstTopic = null;

        for (Topic topic : topicList) {
            if (firstTopic == null) {
                firstTopic = topic;
            }

            TopicDto topicDto = PoDtoMapper.INSTANCE.poToDto(topic);
            TopicOverviewInfo topicOverviewInfo = TopicOverviewInfo.builder().topicInfo(topicDto).categoryListInfo(new ArrayList<>()).build();

            categoryList.stream().filter(category -> category.getTopicId().equals(topic.getTopicId())).forEach(category -> {
                CategoryDto categoryDto = PoDtoMapper.INSTANCE.poToDto(category);
                Optional<ArticleCountInfo> articleCountInfoTemp = articleCountInfoList.stream().filter(articleCountInfo -> articleCountInfo.getCategoryId().equals(category.getCategoryId())).findFirst();
                Integer count = articleCountInfoTemp.map(articleCountInfo -> articleCountInfo.getCount().intValue()).orElse(0);
                // 非管理员隐藏挂载文章数目为 0 的类别
                if (!context.isMaintainer() && count < 1) {
                    return;
                }
                categoryDto.setArticleCount(count);
                topicOverviewInfo.getCategoryListInfo().add(categoryDto);
                topicOverviewInfo.setTotalCount(topicOverviewInfo.getTotalCount() + count);
            });

            if (!context.isMaintainer() && topicOverviewInfo.getCategoryListInfo().isEmpty()) {
                continue;
            }
            result.getTopicOverviewInfoList().add(topicOverviewInfo);
        }

        if (firstTopic != null) {
            // 尝试加载首个 topic 下的文章摘要
            Integer topicId = firstTopic.getTopicId();
            List<Category> accessibleCategoryListForFirstTopic = categoryList.stream().filter(category -> category.getTopicId().equals(topicId)).toList();
            List<Integer> accessibleCategoryIdListForFirstTopic = collectionHelper.filterNonemptyItemAsArrayList(false, accessibleCategoryListForFirstTopic, Category::getCategoryId);
            ArticleSummaryInfo firstTopicArticleSummaryInfo = articleService.findArticleSummaryInfoByCategoryId(accessibleCategoryIdListForFirstTopic, accessibleCategoryListForFirstTopic, context.isGuest() ? null : currentUser.getUserId(), 1, pageSize);
            result.setArticleSummaryInfo(firstTopicArticleSummaryInfo);
        }

        // 追加公告信息(默认拉去全部公告)
        List<Announcement> announcementList = announcementService.fetchAnnouncement(1, 9999);
        List<AnnouncementDto> announcementDtoList = announcementList.stream().map(PoDtoMapper.INSTANCE::poToDto).toList();
        result.setAnnouncementInfoList(announcementDtoList);

        // 加载句子收藏的首页信息
        QuoteInfo quoteInfo = findQuoteInfo(1, pageSize);
        result.setQuoteInfo(quoteInfo);

        return result;
    }

    public ArticleSummaryInfo findArticleSummaryOfTopic(String tid, int currentPage, int pageSize) {
        HyggeRequestContext context = HyggeRequestTracker.getContext();
        User currentUser = context.getCurrentLoginUser();

        Topic topic = topicService.findTopicByTid(tid, false);
        List<Category> accessibleCategoryList = categoryService.getAccessibleCategoryList(currentUser, collectionHelper.createCollection(topic.getTopicId()));

        List<Integer> accessibleCategoryIdList = collectionHelper.filterNonemptyItemAsArrayList(false, accessibleCategoryList, Category::getCategoryId);
        return articleService.findArticleSummaryInfoByCategoryId(accessibleCategoryIdList, accessibleCategoryList, context.isGuest() ? null : currentUser.getUserId(), currentPage, pageSize);
    }

    public ArticleSummaryInfo findArticleSummaryOfCategory(String cid, int currentPage, int pageSize) {
        HyggeRequestContext context = HyggeRequestTracker.getContext();
        User currentUser = context.getCurrentLoginUser();

        Category category = categoryService.findCategoryByCid(cid, true);
        List<Category> accessibleCategoryList;
        if (category != null) {
            accessibleCategoryList = categoryService.getAccessibleCategoryList(currentUser, null);
            accessibleCategoryList = accessibleCategoryList.stream().filter(item -> item.getCategoryId().equals(category.getCategoryId())).toList();
        } else {
            accessibleCategoryList = new ArrayList<>(0);
        }

        List<Integer> accessibleCategoryIdList = collectionHelper.filterNonemptyItemAsArrayList(false, accessibleCategoryList, Category::getCategoryId);
        return articleService.findArticleSummaryInfoByCategoryId(accessibleCategoryIdList, accessibleCategoryList, context.isGuest() ? null : currentUser.getUserId(), currentPage, pageSize);
    }

    public QuoteInfo findQuoteInfo(int currentPage, int pageSize) {
        return quoteService.findQuoteInfo(currentPage, pageSize);
    }

}
