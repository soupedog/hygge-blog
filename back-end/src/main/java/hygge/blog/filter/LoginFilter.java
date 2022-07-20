package hygge.blog.filter;

import hygge.blog.common.HyggeRequestContext;
import hygge.blog.common.HyggeRequestTracker;
import hygge.blog.domain.po.User;
import hygge.blog.filter.base.AbstractHyggeRequestFilter;
import hygge.blog.service.UserServiceImpl;
import hygge.blog.service.UserTokenServiceImpl;
import hygge.commons.exceptions.core.HyggeRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登录过滤器
 *
 * @author Xavier
 * @date 2022/7/20
 */
@Component
public class LoginFilter extends AbstractHyggeRequestFilter {
    @Autowired
    private UserTokenServiceImpl userTokenService;
    @Autowired
    private UserServiceImpl userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        try {
            HyggeRequestContext context = HyggeRequestTracker.getContext();
            String uid = context.getObject(HyggeRequestContext.Key.UID);

            if (parameterHelper.isNotEmpty(uid)) {
                // 非访客用户
                User targetUser = userService.findUserByUid(uid, false);
                String token = context.getObject(HyggeRequestContext.Key.TOKEN);
                parameterHelper.stringNotEmpty("token", (Object) token);

                userTokenService.validateUserToken(token, targetUser, null);

                // 身份验证成功
                context.setCurrentLoginUser(targetUser);
            } else {
                // 访客用户
                context.setGuest(true);
            }

            filterChain.doFilter(request, response);
        } catch (HyggeRuntimeException e) {
            onError(response, e.getMessage(), e);
        } catch (Exception e) {
            onError(response, e);
        }
    }
}
