package hygge.blog.controller;

import hygge.blog.common.HyggeRequestContext;
import hygge.blog.common.HyggeRequestTracker;
import hygge.blog.common.mapper.PoDtoMapper;
import hygge.blog.controller.doc.ArticleControllerDoc;
import hygge.blog.domain.local.bo.HyggeBlogControllerResponse;
import hygge.blog.domain.local.dto.ArticleDto;
import hygge.blog.domain.local.po.Article;
import hygge.blog.domain.local.po.User;
import hygge.blog.service.local.MarkdownContentServiceImpl;
import hygge.blog.service.local.normal.ArticleBrowseLogServiceImpl;
import hygge.blog.service.local.normal.ArticleServiceImpl;
import hygge.commons.annotation.HyggeExpressionForOutputFunction;
import hygge.web.util.log.annotation.ControllerLog;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

/**
 * @author Xavier
 * @date 2022/7/24
 */
@RestController
@RequestMapping(value = "/blog-service/api/main")
public class ArticleController implements ArticleControllerDoc {
    private final ArticleServiceImpl articleService;
    private final ArticleBrowseLogServiceImpl articleBrowseLogService;
    private final MarkdownContentServiceImpl markdownContentService;

    public ArticleController(ArticleServiceImpl articleService, ArticleBrowseLogServiceImpl articleBrowseLogService, MarkdownContentServiceImpl markdownContentService) {
        this.articleService = articleService;
        this.articleBrowseLogService = articleBrowseLogService;
        this.markdownContentService = markdownContentService;
    }

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
    @ControllerLog(outputParamExpressions = {@HyggeExpressionForOutputFunction(name = "title", value = "main == null ? null : main.title")})
    public ResponseEntity<HyggeBlogControllerResponse<ArticleDto>> findArticle(@PathVariable("aid") String aid,
                                                                               @RequestParam(required = false, defaultValue = "false") boolean raw) {
        HyggeRequestContext context = HyggeRequestTracker.getContext();

        ArticleDto result = articleService.findArticleDetailByAid(true, aid);

        if (result != null) {
            // 文本替换(该站点受保护文件，发放访问许可)
            if (!raw) {
                String newContent = markdownContentService.replaceMarkdownImageResource(result.getContent());
                result.setContent(newContent);
            }
            // 浏览量自增
            if (!context.isMaintainer()) {
                articleBrowseLogService.insertArticleBrowseLogAsync(result.getAid(),
                        result.getTitle(),
                        context.getObject(HyggeRequestContext.Key.IP_ADDRESS),
                        Optional.ofNullable(context.getCurrentLoginUser()).map(User::getUserId).orElse(null),
                        context.getObject(HyggeRequestContext.Key.USER_AGENT));
            }
        }
        return (ResponseEntity<HyggeBlogControllerResponse<ArticleDto>>) success(result);
    }
}
