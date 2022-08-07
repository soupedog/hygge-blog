package hygge.blog.controller;

import hygge.blog.common.HyggeRequestContext;
import hygge.blog.common.HyggeRequestTracker;
import hygge.blog.controller.doc.EntranceControllerDoc;
import hygge.blog.domain.bo.HyggeBlogControllerResponse;
import hygge.blog.domain.dto.UserDto;
import hygge.blog.domain.dto.UserTokenDto;
import hygge.blog.domain.mapper.PoDtoMapper;
import hygge.blog.domain.po.User;
import hygge.blog.domain.po.UserToken;
import hygge.blog.service.UserServiceImpl;
import hygge.blog.service.UserTokenServiceImpl;
import hygge.web.template.HyggeWebUtilContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Xavier
 * @date 2022/7/20
 */
@RestController
@RequestMapping(value = "/blog-service/api")
public class EntranceController extends HyggeWebUtilContainer implements EntranceControllerDoc {
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private UserTokenServiceImpl userTokenService;

    @Override
    @PostMapping("/sign/up")
    public ResponseEntity<HyggeBlogControllerResponse<UserDto>> signUp(@RequestBody UserDto userDTO) {
        User user = PoDtoMapper.INSTANCE.dtoToPo(userDTO);
        user = userService.saveUser(user);
        UserDto result = PoDtoMapper.INSTANCE.poToDto(user);
        return (ResponseEntity<HyggeBlogControllerResponse<UserDto>>) success(result);
    }

    @Override
    @PostMapping("/sign/in")
    public ResponseEntity<HyggeBlogControllerResponse<UserTokenDto>> signIn(@RequestBody UserDto userDTO) {
        HyggeRequestContext context = HyggeRequestTracker.getContext();

        UserToken userToken;
        String refreshKey = context.getObject(HyggeRequestContext.Key.REFRESH_KEY);
        if (parameterHelper.isNotEmpty(refreshKey)) {
            // 已登录用户刷新
            String uid = parameterHelper.stringNotEmpty("uid", (Object) context.getObject(HyggeRequestContext.Key.UID));

            userToken = userTokenService.refreshToken(null, uid, refreshKey);


            User user = userService.findUserByUserId(userToken.getUserId(), false);
            context.setCurrentLoginUser(user);
        } else {
            // 登录
            parameterHelper.stringNotEmpty("userName", (Object) userDTO.getUserName());
            parameterHelper.stringNotEmpty("password", (Object) userDTO.getPassword());

            userToken = userTokenService.signIn(userDTO.getUserName(), userDTO.getPassword());
        }

        UserTokenDto result = PoDtoMapper.INSTANCE.poToDto(userToken);

        if (context.getCurrentLoginUser() != null) {
            result.setUser(PoDtoMapper.INSTANCE.poToDto(context.getCurrentLoginUser()));
        }
        return (ResponseEntity<HyggeBlogControllerResponse<UserTokenDto>>) success(result);
    }
}
