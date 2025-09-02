package hygge.blog.filter;

import hygge.blog.common.HyggeRequestContext;
import hygge.blog.common.HyggeRequestTracker;
import hygge.blog.domain.local.enums.UserTypeEnum;
import hygge.blog.domain.local.po.User;
import hygge.blog.filter.base.AbstractHyggeRequestFilter;
import hygge.blog.service.local.normal.UserServiceImpl;
import hygge.blog.service.local.normal.UserTokenServiceImpl;
import hygge.commons.exception.main.HyggeRuntimeException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

/**
 * 登录过滤器
 *
 * @author Xavier
 * @date 2022/7/20
 */
@Component
public class LoginFilter extends AbstractHyggeRequestFilter {
    private final UserTokenServiceImpl userTokenService;
    private final UserServiceImpl userService;

    public LoginFilter(UserTokenServiceImpl userTokenService, UserServiceImpl userService) {
        this.userTokenService = userTokenService;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        try {
            HyggeRequestContext context = HyggeRequestTracker.getContext();
            String uid = context.getObject(HyggeRequestContext.Key.UID);

            if (parameterHelper.isNotEmpty(uid)) {
                User targetUser = userService.findUserByUid(uid, false);
                String token = context.getObject(HyggeRequestContext.Key.TOKEN);
                parameterHelper.stringNotEmpty("token", (Object) token);

                userTokenService.validateUserToken(token, targetUser, null);

                // 身份验证成功
                context.setCurrentLoginUser(targetUser);
                // 非访客用户
                context.setGuest(false);
                // 是否是管理员(不是访客且用户身份是管理员)
                context.setMaintainer(context.getCurrentLoginUser().getUserType().equals(UserTypeEnum.ROOT));
            }

            filterChain.doFilter(request, response);
        } catch (HyggeRuntimeException e) {
            onError(response, e.getMessage(), e);
        } catch (Exception e) {
            onError(response, e);
        }
    }
}
