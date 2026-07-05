package hygge.blog.job.other;

import hygge.blog.job.key.RefreshArticleJobKey;
import hygge.blog.job.key.RefreshFileJobKey;
import hygge.job.HyggeJobContext;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author Xavier
 * @date 2026/7/4
 */
@Getter
@Setter
@Generated
public class HyggeBlogJpaContext<T> extends HyggeJobContext {
    private Page<T> page;
    private boolean noNextPage = false;
    /**
     * 全量数据，如果不为空代表不需要再进行数据拉取了。
     */
    private List<T> fullData;

    public HyggeBlogJpaContext() {
    }

    public HyggeBlogJpaContext(String title, int batchSize, boolean bachAsynchronousEnable) {
        this.title = title;
        this.batchSize = batchSize;
        this.bachAsynchronousEnable = bachAsynchronousEnable;
    }

    public <V> V saveArticleObject(RefreshArticleJobKey key, Object object) {
        return saveObject(key, object);
    }

    public <V> V getArticleObject(RefreshArticleJobKey key) {
        return getObject(key);
    }

    public <V> V saveFileObject(RefreshFileJobKey key, Object object) {
        return saveObject(key, object);
    }

    public <V> V getFileObject(RefreshFileJobKey key) {
        return getObject(key);
    }
}
