package hygge.blog.job.other;

import hygge.job.HyggeJobContext;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

/**
 * @author Xavier
 * @date 2026/7/4
 */
@Getter
@Setter
public class HyggeBlogJpaContext<T> extends HyggeJobContext {
    private Page<T> page;
    private boolean noNextPage = false;
}
