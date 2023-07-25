package hygge.blog.service.local;

import hygge.blog.common.HyggeRequestContext;
import hygge.blog.common.HyggeRequestTracker;
import hygge.blog.domain.local.dto.CategoryDto;
import hygge.blog.domain.local.dto.HomepageFetchResult;
import hygge.blog.domain.local.dto.QuoteInfo;
import hygge.blog.domain.local.dto.TopicDto;
import hygge.blog.domain.local.dto.inner.ArticleSummaryInfo;
import hygge.blog.domain.local.dto.inner.TopicOverviewInfo;
import hygge.blog.common.mapper.PoDtoMapper;
import hygge.blog.domain.local.po.ArticleCountInfo;
import hygge.blog.domain.local.po.Category;
import hygge.blog.domain.local.po.Topic;
import hygge.blog.domain.local.po.User;
import hygge.web.template.HyggeWebUtilContainer;
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
public class HomePageServiceImpl extends HyggeWebUtilContainer {
    @Autowired
    private TopicServiceImpl topicService;
    @Autowired
    private CategoryServiceImpl categoryService;
    @Autowired
    private ArticleServiceImpl articleService;
    @Autowired
    private QuoteServiceImpl quoteService;

    public HomepageFetchResult fetch() {
        HyggeRequestContext context = HyggeRequestTracker.getContext();
        User currentUser = context.getCurrentLoginUser();

        List<Topic> topicList = topicService.findAllTopic();

        List<Category> categoryList = categoryService.getAccessibleCategoryList(currentUser, null);

        List<Integer> accessibleCategoryIdList = collectionHelper.filterNonemptyItemAsArrayList(false, categoryList, Category::getCategoryId);

        List<ArticleCountInfo> articleCountInfoList = articleService.findArticleCountInfo(accessibleCategoryIdList, context.isGuest() ? null : currentUser.getUserId());

        HomepageFetchResult result = HomepageFetchResult.builder().topicOverviewInfoList(new ArrayList<>()).build();
        for (Topic topic : topicList) {
            TopicDto topicDto = PoDtoMapper.INSTANCE.poToDto(topic);
            TopicOverviewInfo topicOverviewInfo = TopicOverviewInfo.builder().topicInfo(topicDto).categoryListInfo(new ArrayList<>()).build();

            for (Category category : categoryList) {
                if (!category.getTopicId().equals(topic.getTopicId())) {
                    continue;
                }
                CategoryDto categoryDto = PoDtoMapper.INSTANCE.poToDto(category);

                Optional<ArticleCountInfo> articleCountInfoTemp = articleCountInfoList.stream().filter(articleCountInfo -> articleCountInfo.getCategoryId().equals(category.getCategoryId())).findFirst();
                Integer count = articleCountInfoTemp.map(articleCountInfo -> articleCountInfo.getCount().intValue()).orElse(0);

                if (!context.isMaintainer() && count < 1) {
                    continue;
                }

                categoryDto.setArticleCount(count);
                topicOverviewInfo.getCategoryListInfo().add(categoryDto);
                topicOverviewInfo.setTotalCount(topicOverviewInfo.getTotalCount() + count);
            }

            if (!context.isMaintainer() && topicOverviewInfo.getCategoryListInfo().isEmpty()) {
                continue;
            }
            result.getTopicOverviewInfoList().add(topicOverviewInfo);
        }

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
