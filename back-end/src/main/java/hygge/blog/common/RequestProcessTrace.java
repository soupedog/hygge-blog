package hygge.blog.common;

/**
 * 请求上下文获取器
 *
 * @author Xavier
 * @date 2022/7/20
 */
public class RequestProcessTrace {
    private static final InheritableThreadLocal<HyggeRequestContext> HYGGE_REQUEST_CONTEXT_THREAD_LOCAL = new InheritableThreadLocal<>();

    public static HyggeRequestContext getContext() {
        HyggeRequestContext fullLinkContext = HYGGE_REQUEST_CONTEXT_THREAD_LOCAL.get();
        if (fullLinkContext == null) {
            HYGGE_REQUEST_CONTEXT_THREAD_LOCAL.set(new HyggeRequestContext());
            fullLinkContext = HYGGE_REQUEST_CONTEXT_THREAD_LOCAL.get();
        }
        return fullLinkContext;
    }

    public static void clean() {
        HYGGE_REQUEST_CONTEXT_THREAD_LOCAL.remove();
    }
}