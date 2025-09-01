package hygge.blog.event.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import hygge.util.UtilCreator;
import hygge.util.definition.JsonHelper;
import org.springframework.context.ApplicationEvent;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Xavier
 * @date 2025/9/1
 */
public abstract class HyggeEvent<T> extends ApplicationEvent {
    protected static final JsonHelper<ObjectMapper> jsonHelper = UtilCreator.INSTANCE.getDefaultJsonHelperInstance(false);
    /**
     * 继承链无法避免 timestamp 初始化，防止和原生 timestamp 属性混淆，此处用 ts 代表事件实际发生时间
     */
    protected long ts;

    public HyggeEvent(T source) {
        super(source);
        this.ts = getTimestamp();
    }

    public HyggeEvent(T source, Long tsOfOccurrence) {
        super(source);
        this.ts = Objects.requireNonNullElseGet(tsOfOccurrence, this::getTimestamp);
    }

    @SuppressWarnings("unchecked")
    public T getActualSource() {
        return (T) source;
    }

    public String toJsonInfo() {
        Map<String, Object> resultTemp = new LinkedHashMap<>();
        resultTemp.put("ts", ts);
        resultTemp.put("source", getActualSource());
        return jsonHelper.formatAsString(resultTemp);
    }
}
