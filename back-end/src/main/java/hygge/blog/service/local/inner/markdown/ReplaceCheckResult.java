package hygge.blog.service.local.inner.markdown;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Xavier
 * @date 2026/5/28
 */
@Getter
@Setter
@Builder
@Generated
@NoArgsConstructor
@AllArgsConstructor
public class ReplaceCheckResult {
    private boolean needReplace;
    private String newResource;
}
