package hygge.blog.controller;

import hygge.blog.common.mapper.PoDtoMapper;
import hygge.blog.controller.doc.HomePageControllerDoc;
import hygge.blog.domain.local.bo.HyggeBlogControllerResponse;
import hygge.blog.domain.local.dto.AnnouncementDto;
import hygge.blog.domain.local.dto.HomepageFetchResult;
import hygge.blog.domain.local.dto.QuoteInfo;
import hygge.blog.domain.local.dto.inner.ArticleSummaryInfo;
import hygge.blog.domain.local.po.Announcement;
import hygge.blog.service.elasticsearch.KeywordSearchServiceImpl;
import hygge.blog.service.local.normal.AnnouncementServiceImpl;
import hygge.blog.service.local.normal.HomePageServiceImpl;
import hygge.web.util.log.annotation.ControllerLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Xavier
 * @date 2022/7/25
 */
@RestController
@RequestMapping(value = "/blog-service/api/main")
public class HomePageController implements HomePageControllerDoc {
    @Autowired
    private HomePageServiceImpl homePageService;
    @Autowired
    private AnnouncementServiceImpl announcementService;
    @Autowired
    private KeywordSearchServiceImpl fuzzySearchService;

    @Override
    @GetMapping("/home/fetch")
    @ControllerLog(enable = false)
    public ResponseEntity<HyggeBlogControllerResponse<HomepageFetchResult>> homepageFetch(@RequestParam(required = false, defaultValue = "5") int pageSize) {
        return (ResponseEntity<HyggeBlogControllerResponse<HomepageFetchResult>>) success(homePageService.fetch(pageSize));
    }

    @Override
    @GetMapping("/home/fetch/topic/overview")
    @ControllerLog(enable = false)
    public ResponseEntity<HyggeBlogControllerResponse<HomepageFetchResult>> topicOverviewFetch() {
        return (ResponseEntity<HyggeBlogControllerResponse<HomepageFetchResult>>) success(homePageService.fetch(null));
    }

    @Override
    @GetMapping("/home/fetch/topic/{tid}")
    @ControllerLog(enable = false)
    public ResponseEntity<HyggeBlogControllerResponse<ArticleSummaryInfo>> topicInfoFetch(@PathVariable("tid") String tid,
                                                                                          @RequestParam(required = false, defaultValue = "1") int currentPage,
                                                                                          @RequestParam(required = false, defaultValue = "5") int pageSize) {
        return (ResponseEntity<HyggeBlogControllerResponse<ArticleSummaryInfo>>) success(homePageService.findArticleSummaryOfTopic(tid, currentPage, pageSize));
    }

    @Override
    @GetMapping("/home/fetch/category/{cid}")
    @ControllerLog(enable = false)
    public ResponseEntity<HyggeBlogControllerResponse<ArticleSummaryInfo>> categoryInfoFetch(@PathVariable("cid") String cid,
                                                                                             @RequestParam(required = false, defaultValue = "1") int currentPage,
                                                                                             @RequestParam(required = false, defaultValue = "5") int pageSize) {
        return (ResponseEntity<HyggeBlogControllerResponse<ArticleSummaryInfo>>) success(homePageService.findArticleSummaryOfCategory(cid, currentPage, pageSize));
    }

    @Override
    @GetMapping("/home/fetch/quote")
    @ControllerLog(enable = false)
    public ResponseEntity<HyggeBlogControllerResponse<QuoteInfo>> quoteInfoFetch(@RequestParam(required = false, defaultValue = "1") int currentPage,
                                                                                 @RequestParam(required = false, defaultValue = "5") int pageSize) {
        return (ResponseEntity<HyggeBlogControllerResponse<QuoteInfo>>) success(homePageService.findQuoteInfo(currentPage, pageSize));
    }

    @Override
    @GetMapping("/home/search/article")
    public ResponseEntity<HyggeBlogControllerResponse<ArticleSummaryInfo>> articleFuzzySearch(@RequestParam(value = "keyword") String keyword,
                                                                                              @RequestParam(required = false, defaultValue = "1") int currentPage,
                                                                                              @RequestParam(required = false, defaultValue = "5") int pageSize) {
        return (ResponseEntity<HyggeBlogControllerResponse<ArticleSummaryInfo>>) success(fuzzySearchService.articleKeyWordSearch(keyword, currentPage, pageSize));
    }

    @Override
    @GetMapping("/home/search/quote")
    @ControllerLog(enable = false)
    public ResponseEntity<HyggeBlogControllerResponse<QuoteInfo>> keywordSearch(@RequestParam(value = "keyword") String keyword,
                                                                                @RequestParam(required = false, defaultValue = "1") int currentPage,
                                                                                @RequestParam(required = false, defaultValue = "5") int pageSize) {
        return (ResponseEntity<HyggeBlogControllerResponse<QuoteInfo>>) success(fuzzySearchService.quoteKeyWordSearch(keyword, currentPage, pageSize));
    }

    @Override
    @GetMapping("/home/fetch/announcement")
    @ControllerLog(enable = false)
    public ResponseEntity<HyggeBlogControllerResponse<List<AnnouncementDto>>> announcementFetch(@RequestParam(required = false, defaultValue = "1") int currentPage,
                                                                                                @RequestParam(required = false, defaultValue = "100") int pageSize) {

        List<Announcement> resultTemp = announcementService.fetchAnnouncement(currentPage, pageSize);
        List<AnnouncementDto> result = resultTemp.stream().map(PoDtoMapper.INSTANCE::poToDto).toList();
        return (ResponseEntity<HyggeBlogControllerResponse<List<AnnouncementDto>>>) success(result);
    }
}
