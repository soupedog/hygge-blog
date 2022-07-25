package hygge.blog.controller;

import hygge.blog.controller.doc.HomePageControllerDoc;
import hygge.blog.domain.bo.HyggeBlogControllerResponse;
import hygge.blog.domain.dto.inner.TopicOverviewInfo;
import hygge.blog.service.HomePageServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Xavier
 * @date 2022/7/25
 */
@RestController
@RequestMapping(value = "/blog-service/api/home")
public class HomePageController implements HomePageControllerDoc {
    @Autowired
    private HomePageServiceImpl homePageService;

    @Override
    @PostMapping("/fetch")
    public ResponseEntity<HyggeBlogControllerResponse<TopicOverviewInfo>> homepageFetch() {
        return (ResponseEntity<HyggeBlogControllerResponse<TopicOverviewInfo>>) success(homePageService.fetch());
    }
}
