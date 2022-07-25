package hygge.blog.controller.doc;

import hygge.blog.controller.base.HyggeBlogController;
import hygge.blog.domain.bo.HyggeBlogControllerResponse;
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
    @Operation(summary = "主页数据拉取", description = "在主页初次加载是需要拉取的数据")
    ResponseEntity<HyggeBlogControllerResponse<TopicOverviewInfo>> homepageFetch();
}
