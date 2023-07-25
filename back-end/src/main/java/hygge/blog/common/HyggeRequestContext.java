package hygge.blog.common;


import hygge.blog.domain.local.enums.TokenScopeEnum;
import hygge.blog.domain.local.po.User;
import hygge.commons.template.container.base.AbstractHyggeContext;
import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.EnumMap;

/**
 * 请求上下文
 *
 * @author Xavier
 * @date 2022/7/20
 */
@Getter
@Setter
@Generated
@NoArgsConstructor
@AllArgsConstructor
public class HyggeRequestContext extends AbstractHyggeContext<HyggeRequestContext.Key> {
    @Override
    protected void initContainer(int initialCapacity, float loadFactor) {
        this.container = new EnumMap<>(HyggeRequestContext.Key.class);
    }

    /**
     * 服务端首次开始处理请求 UTC 毫秒级 Long 时间戳
     */
    private Long serviceStartTs;
    /**
     * 用户令牌 scope:Web 端,移动端
     */
    private TokenScopeEnum tokenScope;
    /**
     * 当前登录用户信息
     */
    private User currentLoginUser;

    private boolean guest = true;

    private boolean maintainer = false;

    public enum Key {
        UID, TOKEN, REFRESH_KEY, SECRET_KEY, IP_ADDRESS, USER_AGENT
    }
}
