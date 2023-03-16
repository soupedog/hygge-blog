package hygge.blog.filter;

import hygge.blog.common.HyggeRequestContext;
import hygge.blog.common.HyggeRequestTracker;
import hygge.blog.domain.enums.TokenScopeEnum;
import hygge.blog.domain.po.User;
import hygge.blog.filter.base.AbstractHyggeRequestFilter;
import hygge.commons.exception.main.HyggeRuntimeException;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

/**
 * 最外层 filter</br>
 * 这是最初的入口，对 HyggeRequestContext 进行初始化
 *
 * @author Xavier
 * @date 2022/7/20
 */
@Component
public class HyggeRequestFilter extends AbstractHyggeRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        long serviceStartTs = System.currentTimeMillis();
        HyggeRequestContext context = null;
        // 填充服务端信息，允许跨域访问
        enableCrossOrigin(response);

        try {
            switch (request.getMethod().toUpperCase()) {
                case "GET":
                case "POST":
                case "PUT":
                case "DELETE":
                    context = HyggeRequestTracker.getContext();
                    context.setServiceStartTs(serviceStartTs);

                    String uid = parameterHelper.string(request.getHeader("uid"));
                    String token = parameterHelper.string(request.getHeader("token"));
                    String secretKey = parameterHelper.string(request.getHeader("secretKey"));
                    String refreshKey = parameterHelper.string(request.getHeader("refreshKey"));
                    context.saveObject(HyggeRequestContext.Key.UID, uid);
                    context.saveObject(HyggeRequestContext.Key.TOKEN, token);
                    context.saveObject(HyggeRequestContext.Key.SECRET_KEY, secretKey);
                    context.saveObject(HyggeRequestContext.Key.REFRESH_KEY, refreshKey);

                    // x-forwarded-for 为 HTTP 头字段标准化草案中正式提出。详见 https://baike.baidu.com/item/X-Forwarded-For
                    String ipAddress = request.getHeader(HEADER_KEY_REMOTE_ADDR);
                    context.saveObject(HyggeRequestContext.Key.IP_ADDRESS, ipAddress);
                    String userAgent = request.getHeader(HEADER_KEY_HTTP_USER_AGENT);
                    context.saveObject(HyggeRequestContext.Key.USER_AGENT, userAgent);

                    TokenScopeEnum scope = TokenScopeEnum.parse(parameterHelper.stringOfNullable(request.getHeader("scope"), "WEB"));
                    context.setTokenScope(scope);
                    filterChain.doFilter(request, response);
                    break;
                default:
            }
        } catch (HyggeRuntimeException e) {
            onError(response, e.getMessage(), e);
        } catch (Exception e) {
            onError(response, e);
        } finally {
            if (context == null) {
                context = HyggeRequestTracker.getContext();
            }

            if (context.getServiceStartTs() != null) {
                long costTime = System.currentTimeMillis() - context.getServiceStartTs();
                if (costTime > requestTimeLimit.toMillis()) {
                    String requestUri = request.getRequestURI();
                    String ipAddress = parameterHelper.stringOfNullable(context.getObject(HyggeRequestContext.Key.IP_ADDRESS), "unknown");
                    String loginUser = Optional.ofNullable(context.getCurrentLoginUser()).map(User::getUid).orElse("guest");
                    String logInfo = "HyggeRequestFilter slow request " + requestUri + " cost " + costTime + " ms.(uid:" + loginUser + " ip:" + ipAddress + ")";
                    log.warn(logInfo);
                }
            }
            // 清理进程上下文
            HyggeRequestTracker.clean();
        }
    }

    private void enableCrossOrigin(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "OPTIONS,GET,POST,PUT,DELETE");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type,Access-Control-Allow-Headers,Authorization,X-Requested-With,http_user_agent,uid,token,refreshKey,secretKey,scope");
        response.setHeader("Access-Control-Max-Age", "2592000");
    }
}
