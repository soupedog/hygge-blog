package hygge.blog.service.local.normal;

import hygge.blog.domain.local.po.Announcement;
import hygge.blog.repository.database.AnnouncementDao;
import hygge.util.template.HyggeJsonUtilContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Xavier
 * @date 2022/8/7
 */
@Service
public class AnnouncementServiceImpl extends HyggeJsonUtilContainer {
    @Autowired
    private AnnouncementDao announcementDao;

    public List<Announcement> fetchAnnouncement(int currentPage, int pageSize) {
        Sort sort = Sort.by(Sort.Order.asc("createTs"));
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize, sort);
        Page<Announcement> resultTemp = announcementDao.findAll(pageable);

        return resultTemp.getContent();
    }
}
