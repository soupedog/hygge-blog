package hygge.blog.util;

import hygge.blog.HyggeBlogApplication;
import hygge.blog.common.HyggeRequestContext;
import hygge.blog.common.HyggeRequestTracker;
import hygge.blog.repository.database.ArticleDao;
import hygge.blog.domain.dto.ArticleDto;
import hygge.blog.domain.dto.CategoryDto;
import hygge.blog.domain.enums.UserTypeEnum;
import hygge.blog.domain.po.User;
import hygge.blog.service.ArticleServiceImpl;
import hygge.commons.constant.enums.DateTimeFormatModeEnum;
import hygge.util.UtilCreator;
import hygge.util.definition.FileHelper;
import hygge.web.config.ControllerLogAutoConfiguration;
import hygge.web.template.HyggeWebUtilContainer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchRestClientAutoConfiguration;
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
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MyMockBean.class)
@SpringBootTest(
        args = {
                "--logging.level.org.hibernate.type.descriptor.sql.BasicBinder=INFO",
                "--logging.level.org.hibernate.engine.transaction.internal.*=INFO",
                "--hygge.blog.database.showSql=false",
        },
        classes = HyggeBlogApplication.class
)

@EnableAutoConfiguration(exclude = {
        ElasticsearchRestClientAutoConfiguration.class,
        ElasticsearchRepositoriesAutoConfiguration.class,
        ControllerLogAutoConfiguration.class
})
@SuppressWarnings("java:S2699")
@Slf4j
class SynchronizeArticle extends HyggeWebUtilContainer {
    private static final String path = "H:\\Xavier\\Documents\\md文档\\";
    private static final String backup = "H:\\Xavier\\Documents\\md文档backup\\";

    private static final FileHelper fileHelper = UtilCreator.INSTANCE.getDefaultInstance(FileHelper.class);
    @Autowired
    private ArticleDao articleDao;
    @Autowired
    private ArticleServiceImpl articleService;
    private LinkedHashMap<String, String> resultMap = new LinkedHashMap<>();

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
        File backupDirectory = new File(backup + File.separator + timeHelper.format(System.currentTimeMillis(), DateTimeFormatModeEnum.FULL_TRIM));

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("articleId")));

        Page<String> aidListTemp = articleDao.findAidByPageable(pageable);
        List<String> aidList;

        do {
            if (pageable.getPageNumber() != 0) {
                // 不是第一页则进行查询
                aidListTemp = articleDao.findAidByPageable(pageable);
            }

            aidList = aidListTemp.getContent();

            for (String aid : aidList) {
                ArticleDto articleDto = articleService.findArticleDetailByAid(false, aid);
                synchronizeForSingle(hyggeRequestContext, rootDirectory, backupDirectory, articleDto);
            }
            pageable = aidListTemp.nextPageable();
        } while (!aidListTemp.isLast());

        log.info(jsonHelperIndent.formatAsString(resultMap));
        log.info("cost:" + (System.currentTimeMillis() - start) + " ms");
    }

    private void synchronizeForSingle(HyggeRequestContext hyggeRequestContext, File saveDirectory, File backupDirectory, ArticleDto articleDto) {
        String fileName = articleDto.getTitle() + ".md";

        List<File> searchTemp = fileHelper.getFileByFileNameFromDirectoryIgnoreDepth(saveDirectory, fileName);
        if (searchTemp.size() > 0) {
            File localFile = searchTemp.get(0);
            String localContent = fileHelper.getTextFileContent(localFile).toString();
            // 本地远端数据不同
            if (!articleDto.getContent().equals(localContent)) {
                // 本地数据同步到远端
                if (localFile.lastModified() == articleDto.getLastUpdateTs()) {
                    resultMap.put(articleDto.getTitle(), "idle");
                } else if (localFile.lastModified() < articleDto.getLastUpdateTs()) {
                    // 远端数据同步到本地
                    fileHelper.saveTextFile(getFilePathByArticleCategory(backupDirectory, articleDto), articleDto.getTitle(), ".md", localContent);
                    fileHelper.saveTextFile(getFilePathByArticleCategory(saveDirectory, articleDto), articleDto.getTitle(), ".md", articleDto.getContent());
                    resultMap.put(articleDto.getTitle(), "download");
                } else {
                    LinkedHashMap<String, Object> updateData = new LinkedHashMap<>();
                    updateData.put("content", localContent);
                    hyggeRequestContext.setServiceStartTs(localFile.lastModified());
                    fileHelper.saveTextFile(getFilePathByArticleCategory(backupDirectory, articleDto), articleDto.getTitle(), ".md", articleDto.getContent());
                    articleService.updateArticle(articleDto.getAid(), updateData);
                    resultMap.put(articleDto.getTitle(), "upload");
                }
            }
        } else {
            fileHelper.saveTextFile(getFilePathByArticleCategory(saveDirectory, articleDto), articleDto.getTitle(), ".md", articleDto.getContent());
            resultMap.put(articleDto.getTitle(), "download");
        }
    }

    private String getFilePathByArticleCategory(File rootPath, ArticleDto articleDto) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(rootPath.getAbsolutePath());
        stringBuilder.append(File.separator);
        stringBuilder.append(articleDto.getCategoryTreeInfo().getTopicInfo().getTopicName());
        stringBuilder.append(File.separator);
        for (CategoryDto categoryDto : articleDto.getCategoryTreeInfo().getCategoryList()) {
            stringBuilder.append(categoryDto.getCategoryName());
            stringBuilder.append(File.separator);
        }
        parameterHelper.removeStringFormTail(stringBuilder, File.separator, 1);
        return stringBuilder.toString();
    }
}
