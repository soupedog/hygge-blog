package hygge.blog.service.local;

import lombok.Getter;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Xavier
 * @date 2025/11/10
 */
@Getter
public class FileKeyLocalCounter {
    private static final AtomicInteger counter = new AtomicInteger(1);
    private final String fileNo;

    public FileKeyLocalCounter(String fileNo) {
        this.fileNo = fileNo;
    }

    public boolean writeOff() {
        return counter.decrementAndGet() > -1;
    }
}
