package hygge.blog.util;

import hygge.blog.common.HyggeRequestContext;
import hygge.blog.common.HyggeRequestTracker;
import hygge.blog.config.database.DataBaseAutoConfig;
import hygge.blog.config.util.http.HttpHelperAutoConfigurationForSpringBoot3;
import hygge.blog.domain.local.dto.CategoryDto;
import hygge.blog.domain.local.dto.inner.CategoryTreeInfo;
import hygge.blog.domain.local.enums.UserTypeEnum;
import hygge.blog.domain.local.po.Article;
import hygge.blog.domain.local.po.User;
import hygge.blog.repository.database.ArticleDao;
import hygge.blog.service.local.CacheServiceImpl;
import hygge.blog.service.local.EventServiceImpl;
import hygge.blog.service.local.normal.ArticleCountServiceImpl;
import hygge.blog.service.local.normal.ArticleServiceImpl;
import hygge.blog.service.local.normal.CategoryServiceImpl;
import hygge.blog.service.local.normal.QuoteServiceImpl;
import hygge.blog.service.local.normal.TopicServiceImpl;
import hygge.blog.service.local.normal.UserServiceImpl;
import hygge.commons.constant.ConstantParameters;
import hygge.commons.constant.enums.DateTimeFormatModeEnum;
import hygge.util.UtilCreator;
import hygge.util.definition.FileHelper;
import hygge.util.template.HyggeJsonUtilContainer;
import hygge.web.config.HttpHelperAutoConfiguration;
import hygge.web.util.http.configuration.HttpHelperConfiguration;
import hygge.web.util.http.impl.DefaultHttpHelper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 同步本地远端文档工具
 *
 * @author Xavier
 * @date 2022/1/12
 */
@ActiveProfiles("dev")
// Junit 5 中这其实是个可以省略的注解 "https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing.spring-boot-applications"
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MyMockBean.class)
@SpringBootTest(
        // 等效于提供环境变量的键值对
        args = {
                "--logging.level.org.hibernate.engine.transaction.internal.*=WARN",
                "--logging.level.org.hibernate.orm.jdbc.*=WARN",
                "--hygge.blog.database.showSql=false",
        },
        // 在这之外的类不加载，classes 中类的上下顺序对执行有影响
        classes = {
                HttpHelperAutoConfigurationForSpringBoot3.class,
                HttpHelperConfiguration.class,
                HttpHelperAutoConfiguration.class,
                DataBaseAutoConfig.class,
                ArticleServiceImpl.class,
                ArticleCountServiceImpl.class,
                UserServiceImpl.class,
                QuoteServiceImpl.class,
                CategoryServiceImpl.class,
                TopicServiceImpl.class,
                EventServiceImpl.class,
                CacheServiceImpl.class
        }
)
@SuppressWarnings({"java:S2699", "java:S3577"})
@Slf4j
class SynchronizeArticle extends HyggeJsonUtilContainer {
    private static final String path = "G:\\Xavier\\Documents\\md文档\\";
    private static final String backup = "G:\\Xavier\\Documents\\md文档backup\\";

    private static final FileHelper fileHelper = UtilCreator.INSTANCE.getDefaultInstance(FileHelper.class);
    private LinkedHashMap<String, String> resultMap = new LinkedHashMap<>();
    @Autowired
    private ArticleDao articleDao;
    @Autowired
    private ArticleServiceImpl articleService;
    @Autowired
    private CacheServiceImpl cacheService;
    @Autowired
    private DefaultHttpHelper httpHelper;

    @Test
    void doHttpRequest() {
        httpHelper.get("http://www.baidu.com", String.class);
    }

    @Test
    void doSynchronize() {
        log.info("开始同步");
        long start = System.currentTimeMillis();

        HyggeRequestContext hyggeRequestContext = HyggeRequestTracker.getContext();
        User user = new User();
        user.setUserId(1);
        user.setUid("U00000001");
        user.setUserType(UserTypeEnum.ROOT);
        hyggeRequestContext.setCurrentLoginUser(user);

        File rootDirectory = fileHelper.getOrCreateDirectoryIfNotExit(path);
        File backupDirectory = new File(backup + ConstantParameters.FILE_SEPARATOR + timeHelper.format(System.currentTimeMillis(), DateTimeFormatModeEnum.FULL_TRIM));

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

        log.info("info：" + ConstantParameters.LINE_SEPARATOR + jsonHelperIndent.formatAsString(resultMap));
        log.info("cost:" + (System.currentTimeMillis() - start) + " ms");
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
                    resultMap.put(article.getTitle(), "idle");
                } else if (localFile.lastModified() < articleLastUpdateTs) {
                    // 远端数据同步到本地
                    fileHelper.saveTextFile(getFilePathByArticleCategory(backupDirectory, article), article.getTitle(), ".md", localContent);
                    fileHelper.saveTextFile(getFilePathByArticleCategory(saveDirectory, article), article.getTitle(), ".md", article.getContent());
                    resultMap.put(article.getTitle(), "download");
                } else {
                    LinkedHashMap<String, Object> updateData = new LinkedHashMap<>();
                    updateData.put("content", localContent);
                    hyggeRequestContext.setServiceStartTs(localFile.lastModified());
                    fileHelper.saveTextFile(getFilePathByArticleCategory(backupDirectory, article), article.getTitle(), ".md", article.getContent());
                    articleService.updateArticle(article.getAid(), updateData);
                    resultMap.put(article.getTitle(), "upload");
                }
            }
        } else {
            fileHelper.saveTextFile(getFilePathByArticleCategory(saveDirectory, article), article.getTitle(), ".md", article.getContent());
            resultMap.put(article.getTitle(), "download");
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
