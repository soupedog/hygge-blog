package hygge.blog.filter;

import hygge.blog.common.HyggeRequestContext;
import hygge.blog.common.HyggeRequestTracker;
import hygge.blog.domain.local.enums.UserTypeEnum;
import hygge.blog.filter.base.AbstractHyggeRequestFilter;
import hygge.blog.service.local.normal.UserServiceImpl;
import hygge.commons.exception.main.HyggeRuntimeException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * 权限过滤过滤器，
 *
 * @author Xavier
 * @date 2025/5/13
 */
@Component
public class UserRoleFilter extends AbstractHyggeRequestFilter {
    @Autowired
    private UserServiceImpl userService;
    public static final HashMap<String, UserTypeEnum> userRoleMapping = new HashMap<>();

    static {
        userRoleMapping.put("/blog-service/api/main/cache", UserTypeEnum.ROOT);
        userRoleMapping.put("/blog-service/api/main/file", UserTypeEnum.ROOT);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        try {
            UserTypeEnum targetType = userRoleMapping.get(request.getRequestURI());
            if (targetType != null) {
                // 存在权限要求的话
                HyggeRequestContext context = HyggeRequestTracker.getContext();
                userService.checkUserRight(context.getCurrentLoginUser(), targetType);
            }

            filterChain.doFilter(request, response);
        } catch (HyggeRuntimeException e) {
            onError(response, e.getMessage(), e);
        } catch (Exception e) {
            onError(response, e);
        }
    }
}
