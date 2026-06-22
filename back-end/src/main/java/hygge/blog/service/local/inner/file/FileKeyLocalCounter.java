package hygge.blog.service.local.inner.file;

import lombok.Getter;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Xavier
 * @date 2025/11/10
 */
@Getter
public class FileKeyLocalCounter {
    private final AtomicInteger counter;
    private final String fileNo;

    public FileKeyLocalCounter(Integer maxCount, String fileNo) {
        this.counter = new AtomicInteger(maxCount);
        this.fileNo = fileNo;
    }

    public boolean writeOff() {
        return counter.decrementAndGet() > -1;
    }
}
