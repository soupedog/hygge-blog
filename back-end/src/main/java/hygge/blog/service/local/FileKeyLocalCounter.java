package hygge.blog.service.local;

import lombok.Getter;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Xavier
 * @date 2025/11/10
 */
@Getter
public record FileKeyLocalCounter(String fileNo) {
    private static final AtomicInteger counter = new AtomicInteger(1);

    public boolean writeOff() {
        return counter.decrementAndGet() > -1;
    }
}
