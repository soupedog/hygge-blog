package hygge.blog.event.base;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Xavier
 * @date 2026/5/29
 */
@Getter
@Setter
public abstract class BaseRefreshEventInfo {
    /**
     * 是否为全体刷新，默认未单个刷新。为 true 时，自主拉取并刷新目标 type 的所有对象
     */
    protected boolean isForAll;
}
