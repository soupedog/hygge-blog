package hygge.blog.controller;

import hygge.blog.controller.doc.ArticleControllerDoc;
import hygge.blog.domain.bo.HyggeBlogControllerResponse;
import hygge.blog.domain.dto.ArticleDto;
import hygge.blog.domain.mapper.PoDtoMapper;
import hygge.blog.domain.po.Article;
import hygge.blog.service.ArticleServiceImpl;
import hygge.commons.templates.core.annotation.HyggeExpressionInfo;
import hygge.web.utils.log.annotation.ControllerLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author Xavier
 * @date 2022/7/24
 */
@RestController
@RequestMapping(value = "/blog-service/api/main")
public class ArticleController implements ArticleControllerDoc {
    @Autowired
    private ArticleServiceImpl articleService;

    @Override
    @PostMapping("/article")
    public ResponseEntity<HyggeBlogControllerResponse<ArticleDto>> createArticle(@RequestBody ArticleDto articleDto) {
        Article resultTemp = articleService.createArticle(articleDto);
        ArticleDto result = PoDtoMapper.INSTANCE.poToDto(resultTemp);
        return (ResponseEntity<HyggeBlogControllerResponse<ArticleDto>>) success(result);
    }

    @Override
    @PutMapping("/article/{aid}")
    public ResponseEntity<HyggeBlogControllerResponse<ArticleDto>> updateArticle(@PathVariable("aid") String aid, @RequestBody Map<String, Object> data) {
        Article resultTemp = articleService.updateArticle(aid, data);
        ArticleDto result = PoDtoMapper.INSTANCE.poToDto(resultTemp);
        return (ResponseEntity<HyggeBlogControllerResponse<ArticleDto>>) success(result);
    }

    @Override
    @GetMapping("/article/{aid}")
    @ControllerLog(outputParamExpressions = {@HyggeExpressionInfo(rootObjectName = "#root", name = "title", value = "main.title")})
    public ResponseEntity<HyggeBlogControllerResponse<ArticleDto>> findArticle(@PathVariable("aid") String aid) {
        return (ResponseEntity<HyggeBlogControllerResponse<ArticleDto>>) success(articleService.findArticleDetailByAid(aid));
    }
}
