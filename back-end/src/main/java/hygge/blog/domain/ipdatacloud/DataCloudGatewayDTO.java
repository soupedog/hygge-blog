package hygge.blog.domain.ipdatacloud;

import hygge.blog.domain.ipdatacloud.inner.DataCloudIpInfoDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * IP 数据云请求返回值
 *
 * @author Xavier
 * @date 2025/12/22
 */
@Getter
@Setter
@Builder
@Generated
@NoArgsConstructor
@AllArgsConstructor
public class DataCloudGatewayDTO {
    private int code;
    private String msg;
    private DataCloudIpInfoDTO data;
}
