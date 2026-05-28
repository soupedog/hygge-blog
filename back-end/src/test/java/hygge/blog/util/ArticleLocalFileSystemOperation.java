package hygge.blog.util;

import com.vladsch.flexmark.util.ast.NodeVisitor;
import hygge.blog.common.HyggeRequestContext;
import hygge.blog.common.HyggeRequestTracker;
import hygge.blog.domain.local.dto.CategoryDto;
import hygge.blog.domain.local.dto.inner.CategoryTreeInfo;
import hygge.blog.domain.local.enums.AccessConditionTypeEnum;
import hygge.blog.domain.local.enums.ArticleStateEnum;
import hygge.blog.domain.local.enums.UserTypeEnum;
import hygge.blog.domain.local.po.Article;
import hygge.blog.domain.local.po.Category;
import hygge.blog.domain.local.po.User;
import hygge.blog.repository.database.ArticleDao;
import hygge.blog.repository.elasticsearch.SearchingCacheDao;
import hygge.blog.service.elasticsearch.RefreshElasticSearchServiceImpl;
import hygge.blog.service.local.CacheServiceImpl;
import hygge.blog.service.local.MarkdownContentServiceImpl;
import hygge.blog.service.local.inner.markdown.ImageResourceServerToLocalVisitor;
import hygge.blog.service.local.normal.ArticleServiceImpl;
import hygge.blog.service.local.normal.CategoryServiceImpl;
import hygge.blog.service.local.normal.PermissionServiceImpl;
import hygge.commons.constant.ConstantParameters;
import hygge.commons.constant.enums.DateTimeFormatModeEnum;
import hygge.util.UtilCreator;
import hygge.util.definition.FileHelper;
import hygge.util.template.HyggeJsonUtilContainer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 本地文件系统与文档相关的操作工具
 *
 * @author Xavier
 * @date 2022/1/12
 */
@ActiveProfiles("dev")
// Junit 5 中这其实是个可以省略的注解 "https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing.spring-boot-applications"
@ExtendWith(SpringExtension.class)
@SpringBootTest(
        // 等效于提供环境变量的键值对
        args = {
                "--logging.level.org.hibernate.engine.transaction.internal.*=WARN",
                "--logging.level.org.hibernate.orm.jdbc.*=WARN",
                "--hygge.blog.database.showSql=false",
        }
)
@SuppressWarnings({"java:S2699", "java:S3577"})
@Slf4j
class ArticleLocalFileSystemOperation extends HyggeJsonUtilContainer {
    // 本地不需要 ES 支持
    @MockBean
    private RefreshElasticSearchServiceImpl refreshElasticSearchService;
    @MockBean
    private SearchingCacheDao searchingCacheDao;
    @MockBean
    private ElasticsearchOperations elasticsearchOperations;

    private static final String path = "G:\\Xavier\\Documents\\md文档\\";
    private static final String backupPath = "G:\\Xavier\\Documents\\md文档backup\\";
    private static final String staticBlogPath = "G:\\Xavier\\Documents\\fuwari\\";

    private static final FileHelper fileHelper = UtilCreator.INSTANCE.getDefaultInstance(FileHelper.class);
    private LinkedHashMap<String, String> resultMapForSynchronize = new LinkedHashMap<>();
    private LinkedHashMap<String, String> resultMapForBuild = new LinkedHashMap<>();
    private List<String> fileNoOfNeedCopy = new ArrayList<>();

    @Autowired
    private ArticleDao articleDao;
    @Autowired
    private ArticleServiceImpl articleService;
    @Autowired
    private CacheServiceImpl cacheService;
    @Autowired
    private CategoryServiceImpl categoryService;
    @Autowired
    private MarkdownContentServiceImpl markdownContentService;

    /**
     * 自动同步本地 markdown 和 数据库端文章。
     * 文件名和目录位置相匹配则认为是同一篇文章，根据最后修改时间戳最新的覆盖旧的。
     * 可能本地自动覆盖服务端，也可能服务端覆盖本地端。
     */
    @Test
    void doSynchronize() {
        resultMapForSynchronize.clear();
        log.info("开始同步");
        long start = System.currentTimeMillis();

        HyggeRequestContext hyggeRequestContext = HyggeRequestTracker.getContext();
        User user = new User();
        user.setUserId(1);
        user.setUid("U00000001");
        user.setUserType(UserTypeEnum.ROOT);
        hyggeRequestContext.setCurrentLoginUser(user);

        File rootDirectory = fileHelper.getOrCreateDirectoryIfNotExit(path);
        File backupDirectory = new File(backupPath + ConstantParameters.FILE_SEPARATOR + timeHelper.format(System.currentTimeMillis(), DateTimeFormatModeEnum.FULL_TRIM));

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("articleId")));

        Page<Article> articleListTemp = articleDao.findAll(pageable);
        log.info("共计 {} 篇文章待检测。", articleListTemp.getTotalElements());
        do {
            if (pageable.getPageNumber() != 0) {
                // 不是第一页则进行查询
                articleListTemp = articleDao.findAll(pageable);
            }

            List<Article> articleList = articleListTemp.getContent();

            for (Article article : articleList) {
                synchronizeForSingle(hyggeRequestContext, rootDirectory, backupDirectory, article);
            }

            log.info("完成第 {} 页比对，准备进入下一页。", pageable.getPageNumber() + 1);
            pageable = articleListTemp.nextPageable();
        } while (!articleListTemp.isLast());

        log.info("info：" + ConstantParameters.LINE_SEPARATOR + jsonHelperIndent.formatAsString(resultMapForSynchronize));
        log.info("cost:" + (System.currentTimeMillis() - start) + " ms");
    }

    /**
     * 为服务端的公开文章构建本地的 fuwari 标准资源
     */
    @Test
    void buildFuwariResource() {
        fileNoOfNeedCopy.clear();
        resultMapForBuild.clear();
        log.info("开始构建");
        long start = System.currentTimeMillis();

        HyggeRequestContext hyggeRequestContext = HyggeRequestTracker.getContext();
        User user = new User();
        user.setUserId(1);
        user.setUid("U00000001");
        user.setUserType(UserTypeEnum.ROOT);
        hyggeRequestContext.setCurrentLoginUser(user);

        ImageResourceServerToLocalVisitor imageVisitor = markdownContentService.getImageResourceServerToLocalVisitor(staticBlogPath + "images");

        NodeVisitor nodeVisitor = markdownContentService.getImageNodeVisitor(imageVisitor);

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("articleId")));

        Page<Article> articleListTemp = articleDao.findAll(pageable);
        log.info("共计 {} 篇文章待检测。", articleListTemp.getTotalElements());
        do {
            if (pageable.getPageNumber() != 0) {
                // 不是第一页则进行查询
                articleListTemp = articleDao.findAll(pageable);
            }

            List<Article> articleList = articleListTemp.getContent();

            for (Article article : articleList) {
                buildForSingle(nodeVisitor, article);
            }

            log.info("完成第 {} 页检测，准备进入下一页。", pageable.getPageNumber() + 1);
            pageable = articleListTemp.nextPageable();
        } while (!articleListTemp.isLast());

        log.info("info：" + ConstantParameters.LINE_SEPARATOR + jsonHelperIndent.formatAsString(resultMapForSynchronize));
        log.info("cost:" + (System.currentTimeMillis() - start) + " ms");
    }

    private void buildForSingle(NodeVisitor nodeVisitor, Article article) {
        // 非激活状态，不构建资源
        if (!article.getArticleState().equals(ArticleStateEnum.ACTIVE)) {
            return;
        }

        Category category = categoryService.findCategoryByCategoryId(article.getCategoryId(), false);
        boolean isPublic = PermissionServiceImpl._PUBLIC.getPermissionId().equals(category.getPermissionId());

        if (!isPublic) {
            return;
        }

        // TODO 改造成 fileNo
        fileNoOfNeedCopy.add(article.getCoverFileNo());

        String title, ts, description, tag, categoryStringVal, coverImage;

        title = article.getTitle();
        ts = timeHelper.format(article.getCreateTs().getTime(), DateTimeFormatModeEnum.DATE);
        description = article.getSummary();
        tag = category.getCategoryName();

        CategoryTreeInfo categoryTreeInfo = cacheService.getCategoryTreeFormCurrent(article.getCategoryId());
        if (categoryTreeInfo.getCategoryList().size() > 1) {
            categoryStringVal = categoryTreeInfo.getCategoryList().get(0).getCategoryName();
        } else {
            categoryStringVal = categoryTreeInfo.getTopicInfo().getTopicName();
        }

        coverImage = "";

        String markdownInfo = String.format("---\n" +
                        "title: %s\n" +
                        "published: %s\n" +
                        "description: %s\n" +
                        "tags: [ %s ]\n" +
                        "category: %s\n" +
//                        "image: \"%s\"\n" +
                        "draft: false\n" +
                        "---\n\n",
                title,
                ts,
                description,
                tag,
                categoryStringVal
//                coverImage
        );

        String newContent = markdownInfo + markdownContentService.markdownServerToLocal(nodeVisitor, article.getContent());
        fileHelper.saveTextFile(staticBlogPath + tag, title, ".md", newContent);
    }


    private void synchronizeForSingle(HyggeRequestContext hyggeRequestContext, File saveDirectory, File backupDirectory, Article article) {
        String fileName = article.getTitle() + ".md";
        long articleLastUpdateTs = article.getLastUpdateTs().getTime();

        List<File> searchTemp = fileHelper.getFileByFileNameFromDirectoryIgnoreDepth(saveDirectory, fileName);
        if (!searchTemp.isEmpty()) {
            File localFile = searchTemp.get(0);
            String localContent = fileHelper.getTextFileContent(localFile).toString();
            // 本地远端数据不同
            if (article.getContent().compareTo(localContent) != 0) {
                // 本地数据同步到远端
                if (localFile.lastModified() == articleLastUpdateTs) {
                    resultMapForSynchronize.put(article.getTitle(), "idle");
                } else if (localFile.lastModified() < articleLastUpdateTs) {
                    // 远端数据同步到本地
                    fileHelper.saveTextFile(getFilePathByArticleCategory(backupDirectory, article), article.getTitle(), ".md", localContent);
                    fileHelper.saveTextFile(getFilePathByArticleCategory(saveDirectory, article), article.getTitle(), ".md", article.getContent());
                    resultMapForSynchronize.put(article.getTitle(), "download");
                } else {
                    LinkedHashMap<String, Object> updateData = new LinkedHashMap<>();
                    updateData.put("content", localContent);
                    hyggeRequestContext.setServiceStartTs(localFile.lastModified());
                    fileHelper.saveTextFile(getFilePathByArticleCategory(backupDirectory, article), article.getTitle(), ".md", article.getContent());
                    articleService.updateArticle(article.getAid(), updateData);
                    resultMapForSynchronize.put(article.getTitle(), "upload");
                }
            }
        } else {
            fileHelper.saveTextFile(getFilePathByArticleCategory(saveDirectory, article), article.getTitle(), ".md", article.getContent());
            resultMapForSynchronize.put(article.getTitle(), "download");
        }
    }

    private String getFilePathByArticleCategory(File rootPath, Article article) {
        CategoryTreeInfo categoryTreeInfo = cacheService.getCategoryTreeFormCurrent(article.getCategoryId());

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(rootPath.getAbsolutePath());
        stringBuilder.append(File.separator);
        stringBuilder.append(categoryTreeInfo.getTopicInfo().getTopicName());
        stringBuilder.append(File.separator);
        for (CategoryDto categoryDto : categoryTreeInfo.getCategoryList()) {
            stringBuilder.append(categoryDto.getCategoryName());
            stringBuilder.append(File.separator);
        }
        parameterHelper.removeStringFormTail(stringBuilder, File.separator, 1);
        return stringBuilder.toString();
    }
}
