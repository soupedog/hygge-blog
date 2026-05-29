package hygge.blog.event;

import hygge.blog.domain.local.po.base.FileInfoBase;
import hygge.blog.event.base.BaseRefreshEventInfo;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Xavier
 * @date 2026/5/29
 */
@Getter
@Setter
public class FileCacheRefreshEventInfo extends BaseRefreshEventInfo {
    private String fileNo;
    /**
     * 可空
     */
    private FileInfoBase fileInfoBase;

    public FileCacheRefreshEventInfo(String fileNo) {
        this.fileNo = fileNo;
    }

    public FileCacheRefreshEventInfo() {
        this.isForAll = true;
    }
}
