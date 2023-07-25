package hygge.blog.controller.doc;

import hygge.blog.controller.base.HyggeBlogController;
import hygge.blog.domain.local.bo.HyggeBlogControllerResponse;
import hygge.blog.domain.local.dto.UserDto;
import hygge.blog.domain.local.dto.UserTokenDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

/**
 * @author Xavier
 * @date 2022/7/20
 */
@Tag(name = "用户入口 Controller", description = "用户注册、登陆、身份验证操作")
public interface EntranceControllerDoc extends HyggeBlogController<ResponseEntity<?>> {
    @Operation(summary = "注册新用户", description = "注册新的默认权限用户")
    ResponseEntity<HyggeBlogControllerResponse<UserDto>> signUp(UserDto userDTO);

    @Operation(summary = "用户登录", description = "已注册用户获取身份令牌")
    ResponseEntity<HyggeBlogControllerResponse<UserTokenDto>> signIn(UserDto userDTO);
}
