package hygge.blog.domain.dto.baidu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author Xavier
 * @date 2023/7/6
 * @since 1.0
 */
@Getter
@Setter
@Builder
@Generated
@NoArgsConstructor
@AllArgsConstructor
public class BaiduGatewayDto<T> {
    private String code;
    private T data;
    private Boolean charge;
    private String msg;
    private String ip;
    private String coordsys;
}
