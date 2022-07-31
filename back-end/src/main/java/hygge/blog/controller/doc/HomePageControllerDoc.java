package hygge.blog.controller.doc;

import hygge.blog.controller.base.HyggeBlogController;
import hygge.blog.domain.bo.HyggeBlogControllerResponse;
import hygge.blog.domain.dto.inner.ArticleSummaryInfo;
import hygge.blog.domain.dto.inner.TopicOverviewInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

/**
 * @author Xavier
 * @date 2022/7/25
 */
@Tag(name = "主页 Controller", description = "主页展示所需的各种 API")
public interface HomePageControllerDoc extends HyggeBlogController<ResponseEntity<?>> {
    @Operation(summary = "主页数据拉取", description = "在主页初次加载时需要拉取的数据")
    ResponseEntity<HyggeBlogControllerResponse<TopicOverviewInfo>> homepageFetch();

    @Operation(summary = "查询特定主题下的文章", description = "在主题加载时需要拉取的数据")
    ResponseEntity<HyggeBlogControllerResponse<ArticleSummaryInfo>> topicInfoFetch(String tid, int currentPage, int pageSize);

    @Operation(summary = "查询特定文章类别下的文章", description = "在文章类别加载时需要拉取的数据")
    ResponseEntity<HyggeBlogControllerResponse<ArticleSummaryInfo>> categoryInfoFetch(String cid, int currentPage, int pageSize);
}
