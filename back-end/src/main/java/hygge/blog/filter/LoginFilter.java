package hygge.blog.filter;

import hygge.blog.common.HyggeRequestContext;
import hygge.blog.common.HyggeRequestTracker;
import hygge.blog.common.annotation.RequireAuth;
import hygge.blog.domain.local.enums.UserTypeEnum;
import hygge.blog.domain.local.po.User;
import hygge.blog.filter.base.AbstractHyggeRequestFilter;
import hygge.blog.service.local.normal.UserServiceImpl;
import hygge.blog.service.local.normal.UserTokenServiceImpl;
import hygge.commons.exception.InternalRuntimeException;
import hygge.commons.exception.main.HyggeRuntimeException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PathPatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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
    private final RequestMappingHandlerMapping handlerMapping;
    /**
     * e.g: RoleCheckKey-[ROOT]
     * <p>
     * <p>
     * RoleCheckKey = httpMethod-path
     */
    private final Map<String, Set<UserTypeEnum>> directMatcherMap = new ConcurrentHashMap<>();

    public LoginFilter(UserTokenServiceImpl userTokenService, UserServiceImpl userService, RequestMappingHandlerMapping handlerMapping) {
        this.userTokenService = userTokenService;
        this.userService = userService;
        this.handlerMapping = handlerMapping;
    }

    @PostConstruct
    public void init() {
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = handlerMapping.getHandlerMethods();

        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
            HandlerMethod method = entry.getValue();
            // 检查是否有 @RequireAuth 注解
            if (method.hasMethodAnnotation(RequireAuth.class)) {
                RequireAuth requireAuth = method.getMethodAnnotation(RequireAuth.class);
                Set<UserTypeEnum> userTypeEnumSet = new HashSet<>();
                userTypeEnumSet.addAll(Arrays.asList(requireAuth.userType()));

                PathPatternsRequestCondition pathPatternsCondition = entry.getKey().getPathPatternsCondition();
                // getPatternValues() 返回的非空
                Set<String> paths = pathPatternsCondition.getPatternValues();
                String httpMethod = getTypeByMethod(method);

                for (String path : paths) {
                    if (httpMethod == null || path.contains("{")) {
                        // 不允许带通配符的方法、不被 @GetMapping 等 @XXXMapping 标记的方法使用该注解
                        throw new InternalRuntimeException("@RequireAuth can't add to path:" + path + ".");
                    }
                    directMatcherMap.put(getRoleCheckKey(httpMethod, path), userTypeEnumSet);
                }
            }
        }

        String logInfo = "Permission verification for automatic registration:" + jsonHelper.formatAsString(directMatcherMap);
        log.info(logInfo);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        try {
            String httpMethod = request.getMethod();
            String path = request.getRequestURI();

            Set<UserTypeEnum> roleRequireSet = directMatcherMap.get(getRoleCheckKey(httpMethod, path));
            boolean needPreCheckRole = roleRequireSet != null;

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

            if (needPreCheckRole) {
                // 需要权限预检查
                if (!roleRequireSet.isEmpty()) {
                    UserTypeEnum[] typeEnumsArray = roleRequireSet.toArray(UserTypeEnum[]::new);
                    userService.checkUserRight(context.getCurrentLoginUser(), typeEnumsArray);
                }
                // TODO 目前没有 ROOT 以外的类型，有需要时再加
            }

            filterChain.doFilter(request, response);
        } catch (HyggeRuntimeException e) {
            onError(response, e.getMessage(), e);
        } catch (Exception e) {
            onError(response, e);
        }
    }

    private String getTypeByMethod(HandlerMethod method) {
        if (method.hasMethodAnnotation(GetMapping.class)) {
            return "GET";
        } else if (method.hasMethodAnnotation(PostMapping.class)) {
            return "POST";
        } else if (method.hasMethodAnnotation(PatchMapping.class)) {
            return "PATCH";
        } else if (method.hasMethodAnnotation(PutMapping.class)) {
            return "PUT";
        } else if (method.hasMethodAnnotation(DeleteMapping.class)) {
            return "DELETE";
        } else {
            return null;
        }
    }

    private String getRoleCheckKey(String httpMethod, String path) {
        return httpMethod + "-" + path;
    }
}
