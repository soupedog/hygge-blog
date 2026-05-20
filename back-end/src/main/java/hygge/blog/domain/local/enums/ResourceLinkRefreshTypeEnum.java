package hygge.blog.domain.local.enums;

/**
 * @author Xavier
 * @date 2026/5/20
 */
public enum ResourceLinkRefreshTypeEnum {

    /**
     * 旧资源切换到新版 API 资源
     */
    OLD_TO_API,
    API_TO_NGINX,
    NGINX_TO_API,
    ;
}
