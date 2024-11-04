package hygge.blog.util;

import hygge.util.bo.configuration.arranger.PropertiesArrangerConfiguration;
import hygge.util.impl.PropertiesConfigurationArranger;
import hygge.util.template.HyggeJsonUtilContainer;

import java.io.File;
import java.util.StringJoiner;

/**
 * 整理配置项文件
 *
 * @author Xavier
 * @date 2024/11/4
 * @since 1.0
 */
public class OrganizeConfiguration extends HyggeJsonUtilContainer {
    public static void main(String[] args) {
        String absolutePath = new StringJoiner(File.separator)
                .add(System.getProperty("user.dir"))
                .add("back-end")
                .add("src")
                .add("main")
                .add("resources")
                .toString();

        PropertiesArrangerConfiguration configuration = new PropertiesArrangerConfiguration(
                // 指定这两个配置项优先展示
                collectionHelper.createCollection("spring.profiles.active", "spring.application.name")
        );
        configuration.setSkipFileNameList(collectionHelper.createCollection("application.properties"));

        new PropertiesConfigurationArranger().organizePropertiesFiles(absolutePath, configuration);
    }
}
