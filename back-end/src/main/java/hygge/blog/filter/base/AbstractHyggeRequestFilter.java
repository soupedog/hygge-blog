package hygge.blog.filter.base;

import hygge.commons.exceptions.code.HyggeInfo;
import hygge.utils.UtilsCreator;
import hygge.utils.definitions.ParameterHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

/**
 * Hygge Blog 过滤器基类
 *
 * @author Xavier
 * @date 2022/7/20
 */
@Component
public abstract class AbstractHyggeRequestFilter extends OncePerRequestFilter {
    protected static final Logger log = LoggerFactory.getLogger(AbstractHyggeRequestFilter.class);
    protected static final ParameterHelper parameterHelper = UtilsCreator.INSTANCE.getDefaultInstance(ParameterHelper.class);

    /**
     * 慢请求耗时门槛(毫秒)
     */
    @DurationUnit(ChronoUnit.MILLIS)
    protected static Duration requestTimeLimit = Duration.ofMillis(200);

    public static final String HEADER_KEY_REMOTE_ADDR = "remote_addr";
    public static final String HEADER_KEY_HTTP_USER_AGENT = "http_user_agent";

    protected void onError(HttpServletResponse response, String message, HyggeInfo hyggeInfo) {
        initResponse(response, hyggeInfo.getHyggeCode().getCode(), message == null ? hyggeInfo.getHyggeCode().getPublicMessage() : message);
    }

    protected void onError(HttpServletResponse response, Exception e) {
        initResponse(response, 500, e.getMessage());
    }

    protected void initResponse(HttpServletResponse response, Number stateCode, String message) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        response.setStatus(200);
        String responseBody = "{" + System.lineSeparator() +
                "    \"code\":" + stateCode + "," + System.lineSeparator() +
                "    \"msg\":\"" + message + "\"" + System.lineSeparator() +
                "}";
        try (PrintWriter out = response.getWriter()) {
            out.append(responseBody);
        } catch (IOException e2) {
            log.error(responseBody, e2);
        }
    }

    public static Duration getRequestTimeLimit() {
        return requestTimeLimit;
    }

    public static void setRequestTimeLimit(Duration requestTimeLimit) {
        AbstractHyggeRequestFilter.requestTimeLimit = requestTimeLimit;
    }
}
