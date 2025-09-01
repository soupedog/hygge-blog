package hygge.blog.event.listener.base;

import hygge.blog.event.base.HyggeEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;

/**
 * @author Xavier
 * @date 2025/9/1
 */
@Slf4j
public abstract class HyggeEventListener<T extends HyggeEvent<?>> implements ApplicationListener<T> {

    @Override
    public void onApplicationEvent(T event) {
        recordEventInfo(event);
        handleEvent(event);
    }

    /**
     * 获取当前监听器名称，用于自动日志记录
     */
    protected abstract String getListenerName();

    /**
     * 处理事件的具体方法。执行该方法前会先自动执行
     */
    protected abstract void handleEvent(T event);


    protected void recordEventInfo(T event) {
        String logInfo = getListenerName() + " start processing event : " + event.toJsonInfo();
        log.info(logInfo);
    }
}
