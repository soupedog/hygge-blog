package hygge.blog.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Xavier
 * @date 2022/7/17
 */
@Schema(title = "服务端响应主协议")
@Getter
@Setter
@Generated
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HyggeBlogControllerResponse<T> {
    /**
     * 自定义业务码
     */
    @Schema(title = "自定义业务码")
    protected Integer code;
    /**
     * 提示信息
     */
    @Schema(title = "提示信息，通常在请求异常是才存在")
    protected String msg;
    /**
     * 返回核心内容
     */
    @Schema(title = "服务端响应子协议，不固定与具体业务有关")
    protected T main;
}
