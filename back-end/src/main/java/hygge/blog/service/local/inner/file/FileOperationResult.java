package hygge.blog.service.local.inner.file;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Xavier
 * @date 2026/5/21
 */
@Getter
@Setter
@Builder
@Generated
@NoArgsConstructor
@AllArgsConstructor
public class FileOperationResult {
    private String msg;
    private Throwable throwable;
    private ResultType resultType;

    public enum ResultType {
        SUCCESS,
        ALREADY_EXISTS,
        FAIL
    }
}
