package hygge.blog.service.local;

import hygge.blog.common.HyggeRequestContext;
import hygge.blog.common.HyggeRequestTracker;
import hygge.blog.repository.database.UserTokenDao;
import hygge.blog.domain.local.bo.BlogSystemCode;
import hygge.blog.domain.local.po.User;
import hygge.blog.domain.local.po.UserToken;
import hygge.commons.exception.LightRuntimeException;
import hygge.web.template.HyggeWebUtilContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Xavier
 * @date 2022/7/20
 */
@Service
public class UserTokenServiceImpl extends HyggeWebUtilContainer {
    @Autowired
    private UserTokenDao userTokenDao;
    @Autowired
    private UserServiceImpl userService;

    public UserToken signIn(String userName, String password) {
        parameterHelper.stringNotEmpty("userName", (Object) userName);
        parameterHelper.stringNotEmpty("password", (Object) password);

        User user = userService.findUserByUserName(userName);
        if (user == null) {
            throw new LightRuntimeException(String.format("User(%s) was not found.", userName), BlogSystemCode.USER_NOT_FOUND);
        }

        if (!user.getPassword().equals(password)) {
            throw new LightRuntimeException(BlogSystemCode.LOGIN_FAIL.getPublicMessage(), BlogSystemCode.LOGIN_FAIL);
        }
        HyggeRequestContext context = HyggeRequestTracker.getContext();
        UserToken userToken = userTokenDao.findUserTokenByUserIdAndAndScope(user.getUserId(), context.getTokenScope());

        if (userToken == null) {
            userToken = UserToken.builder()
                    .userId(user.getUserId())
                    .scope(context.getTokenScope())
                    .build();
        }

        userToken.refresh(context.getServiceStartTs());
        userToken = userTokenDao.save(userToken);

        context.setCurrentLoginUser(user);
        return userToken;
    }

    public void validateUserToken(String token, User targetLoginUser, String targetUid) {
        HyggeRequestContext context = HyggeRequestTracker.getContext();

        UserToken userToken = getUserToken(targetLoginUser, targetUid, context);

        if (userToken.getDeadline().getTime() < System.currentTimeMillis()) {
            throw new LightRuntimeException("User token has expired.", BlogSystemCode.TOKEN_HAS_EXPIRED);
        }

        if (!userToken.getToken().equals(token)) {
            throw new LightRuntimeException("Unexpected userToken.", BlogSystemCode.LOGIN_ILLEGAL);
        }
    }

    public UserToken refreshToken(User targetLoginUser, String targetUid, String refreshKey) {
        HyggeRequestContext context = HyggeRequestTracker.getContext();

        UserToken userToken = getUserToken(targetLoginUser, targetUid, context);

        if (!userToken.getRefreshKey().equals(refreshKey)) {
            throw new LightRuntimeException("Unexpected refreshKey.", BlogSystemCode.LOGIN_REFRESH_TOKEN_FAIL);
        }

        userToken.refresh(context.getServiceStartTs());
        return userTokenDao.save(userToken);
    }

    private UserToken getUserToken(User targetLoginUser, String targetUid, HyggeRequestContext hyggeRequestContext) {
        if (targetLoginUser == null) {
            targetLoginUser = userService.findUserByUid(targetUid, true);
            if (targetLoginUser == null) {
                throw new LightRuntimeException(BlogSystemCode.LOGIN_ILLEGAL.getPublicMessage(), BlogSystemCode.LOGIN_ILLEGAL);
            }
        }

        UserToken userToken = userTokenDao.findUserTokenByUserIdAndAndScope(targetLoginUser.getUserId(), hyggeRequestContext.getTokenScope());
        if (userToken == null) {
            throw new LightRuntimeException(BlogSystemCode.LOGIN_ILLEGAL.getPublicMessage(), BlogSystemCode.LOGIN_ILLEGAL);
        }
        return userToken;
    }
}
