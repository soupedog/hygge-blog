package hygge.blog.controller.doc;

import hygge.blog.controller.base.HyggeBlogController;
import hygge.blog.domain.local.bo.HyggeBlogControllerResponse;
import hygge.blog.domain.local.dto.AnnouncementDto;
import hygge.blog.domain.local.dto.HomepageFetchResult;
import hygge.blog.domain.local.dto.QuoteInfo;
import hygge.blog.domain.local.dto.inner.ArticleSummaryInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * @author Xavier
 * @date 2022/7/25
 */
@Tag(name = "主页 Controller", description = "主页展示所需的各种 API")
public interface HomePageControllerDoc extends HyggeBlogController<ResponseEntity<?>> {
    @Operation(summary = "主页数据拉取", description = "在主页初次加载时需要拉取的数据")
    ResponseEntity<HyggeBlogControllerResponse<HomepageFetchResult>> homepageFetch(int pageSize);

    @Operation(summary = "主题概览数据拉取", description = "展示全部主题概要信息时需要拉取的数据")
    ResponseEntity<HyggeBlogControllerResponse<HomepageFetchResult>> topicOverviewFetch();

    @Operation(summary = "查询特定主题下的文章", description = "在主题加载时需要拉取的数据")
    ResponseEntity<HyggeBlogControllerResponse<ArticleSummaryInfo>> topicInfoFetch(String tid, int currentPage, int pageSize);

    @Operation(summary = "查询特定文章类别下的文章", description = "在文章类别加载时需要拉取的数据")
    ResponseEntity<HyggeBlogControllerResponse<ArticleSummaryInfo>> categoryInfoFetch(String cid, int currentPage, int pageSize);

    @Operation(summary = "查询句子收藏", description = "在句子收藏加载时需要拉取的数据")
    ResponseEntity<HyggeBlogControllerResponse<QuoteInfo>> quoteInfoFetch(int currentPage, int pageSize);

    @Operation(summary = "模糊查询文章", description = "根据关键字寻找与之相关的文章")
    ResponseEntity<HyggeBlogControllerResponse<ArticleSummaryInfo>> articleFuzzySearch(String keyword, int currentPage, int pageSize);

    @Operation(summary = "模糊查询句子收藏", description = "根据关键字寻找与之相关的句子收藏")
    ResponseEntity<HyggeBlogControllerResponse<QuoteInfo>> keywordSearch(String keyword, int currentPage, int pageSize);

    @Operation(summary = "公告查询", description = "在公告加载时需要拉取的数据")
    ResponseEntity<HyggeBlogControllerResponse<List<AnnouncementDto>>> announcementFetch(int currentPage, int pageSize);
}
