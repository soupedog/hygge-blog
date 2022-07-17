package hygge.blog.controller.doc;

import hygge.blog.controller.HyggeBlogController;
import hygge.blog.domain.bo.HyggeBlogControllerResponse;
import hygge.blog.domain.dto.UserDTO;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

/**
 * @author Xavier
 * @date 2022/7/17
 */
@Tag(name = "用户操作 Controller", description = "用户的创建修改查询")
public interface UserControllerDoc extends HyggeBlogController<ResponseEntity<?>> {
    @ApiResponse(description = "查询用户")
    ResponseEntity<HyggeBlogControllerResponse<UserDTO>> findUser(String uid);

    ResponseEntity<HyggeBlogControllerResponse<UserDTO>> createUser(UserDTO userDTO);

    ResponseEntity<HyggeBlogControllerResponse<UserDTO>> updateUser(String uid, UserDTO userDTO);
}
