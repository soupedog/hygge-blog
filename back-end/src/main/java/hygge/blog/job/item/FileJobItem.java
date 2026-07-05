package hygge.blog.job.item;

import hygge.blog.domain.local.po.view.FileInfoView;
import hygge.job.BaseHyggeJobItem;

/**
 * @author Xavier
 * @date 2026/7/4
 */
public class FileJobItem<PD> extends BaseHyggeJobItem<FileInfoView, PD, String> {
    public FileJobItem(FileInfoView rawData) {
        super(rawData);
    }

    @Override
    public String getUniqueIdentifier() {
        return getRawData().getFileNo();
    }
}
