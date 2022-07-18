package hygge.blog.controller;

import hygge.blog.controller.doc.UserControllerDoc;
import hygge.blog.domain.bo.HyggeBlogControllerResponse;
import hygge.blog.domain.dto.UserDTO;
import hygge.blog.domain.mapper.UserMapper;
import hygge.blog.domain.po.User;
import hygge.blog.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author Xavier
 * @date 2022/7/17
 */
@RestController
@RequestMapping(value = "/blog-service/api")
public class UserController implements UserControllerDoc {
    @Autowired
    private UserServiceImpl userService;

    @Override
    @PostMapping("/sign/up")
    public ResponseEntity<HyggeBlogControllerResponse<UserDTO>> createUser(@RequestBody UserDTO userDTO) {
        User user = UserMapper.INSTANCE.userDTOToUser(userDTO);
        user = userService.saveUser(user);
        UserDTO result = UserMapper.INSTANCE.userToUserDTO(user);
        return (ResponseEntity<HyggeBlogControllerResponse<UserDTO>>) success(result);
    }

    @Override
    @GetMapping("/main/user/{uid}")
    public ResponseEntity<HyggeBlogControllerResponse<UserDTO>> findUser(@PathVariable("uid") String uid) {
        User user = userService.findUserByUid(uid, true);
        UserDTO result = UserMapper.INSTANCE.userToUserDTO(user);
        return (ResponseEntity<HyggeBlogControllerResponse<UserDTO>>) success(result);
    }

    @Override
    @PutMapping("/main/user/{uid}")
    public ResponseEntity<HyggeBlogControllerResponse<UserDTO>> updateUser(@PathVariable("uid") String uid, @RequestBody Map<String, Object> data) {
        User user = userService.updateUser(uid, data);
        UserDTO result = UserMapper.INSTANCE.userToUserDTO(user);
        return (ResponseEntity<HyggeBlogControllerResponse<UserDTO>>) success(result);
    }
}
