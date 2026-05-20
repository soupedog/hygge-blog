package hygge.blog.service.local;

import hygge.blog.domain.local.enums.ResourceLinkRefreshTypeEnum;
import hygge.blog.domain.local.po.Article;
import hygge.blog.repository.database.ArticleDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Xavier
 * @date 2026/5/20
 */
@Slf4j
@Service
public class ImageLinkRefreshServiceImpl {
    private final ArticleDao articleDao;
    private final MarkdownContentServiceImpl markdownContentService;

    private static final AtomicBoolean conflictFlag = new AtomicBoolean(false);

    public ImageLinkRefreshServiceImpl(ArticleDao articleDao, MarkdownContentServiceImpl markdownContentService) {
        this.articleDao = articleDao;
        this.markdownContentService = markdownContentService;
    }

    public void freshAllArticle(ResourceLinkRefreshTypeEnum refreshType) {
        // 尝试获取执行权（false -> true）
        if (!conflictFlag.compareAndSet(false, true)) {
            throw new IllegalStateException("冲突，请等待未执行完的任务执行完");
        }

        try {
            long startTs = System.currentTimeMillis();
            AtomicInteger totalCount = new AtomicInteger(0);

            Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.asc("articleId")));

            Page<Article> articleTemp = articleDao.findAll(pageable);

            List<Article> articleList = null;
            do {
                if (articleList != null) {
                    articleTemp = articleDao.findAll(articleTemp.nextPageable());
                }
                articleList = articleTemp.getContent();

                articleList.forEach(article -> {

                    freshSingleArticle(refreshType, article);

                    totalCount.incrementAndGet();
                });
            } while (!articleTemp.isLast());

            log.info("已刷新文章数 {} 耗时 {} ms", totalCount.get(), System.currentTimeMillis() - startTs);
        } finally {
            // 执行结束，释放标识
            conflictFlag.set(false);
        }
    }

    public void freshSingleArticle(ResourceLinkRefreshTypeEnum refreshType, Article article) {
        String newContent = null;
        String rawContent = article.getContent();
        switch (refreshType) {
            case OLD_TO_API -> newContent = markdownContentService.markdownImageResourceOldToApi(rawContent);
            case API_TO_NGINX -> newContent = markdownContentService.markdownImageResourceApiToNginx(rawContent);
            case NGINX_TO_API -> newContent = markdownContentService.markdownImageResourceNginxToApi(rawContent);
        }

        if (!rawContent.equals(newContent)) {
            article.setContent(newContent);
            articleDao.save(article);
        }
    }
}
