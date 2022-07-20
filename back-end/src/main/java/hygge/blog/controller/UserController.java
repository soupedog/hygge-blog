package hygge.blog.controller;

import hygge.blog.controller.doc.UserControllerDoc;
import hygge.blog.domain.bo.HyggeBlogControllerResponse;
import hygge.blog.domain.dto.UserDto;
import hygge.blog.domain.mapper.PoToDtoMapper;
import hygge.blog.domain.po.User;
import hygge.blog.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
@RequestMapping(value = "/blog-service/api/main")
public class UserController implements UserControllerDoc {
    @Autowired
    private UserServiceImpl userService;

    @Override
    @GetMapping("/user/{uid}")
    public ResponseEntity<HyggeBlogControllerResponse<UserDto>> findUser(@PathVariable("uid") String uid) {
        User user = userService.findUserByUid(uid, true);
        UserDto result = PoToDtoMapper.INSTANCE.poToDto(user);
        return (ResponseEntity<HyggeBlogControllerResponse<UserDto>>) success(result);
    }

    @Override
    @PutMapping("/user/{uid}")
    public ResponseEntity<HyggeBlogControllerResponse<UserDto>> updateUser(@PathVariable("uid") String uid, @RequestBody Map<String, Object> data) {
        userService.notGuest();

        User user = userService.updateUser(uid, data);
        UserDto result = PoToDtoMapper.INSTANCE.poToDto(user);
        return (ResponseEntity<HyggeBlogControllerResponse<UserDto>>) success(result);
    }
}
