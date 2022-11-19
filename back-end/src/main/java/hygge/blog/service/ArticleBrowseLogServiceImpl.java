package hygge.blog.service;

import hygge.blog.dao.ArticleBrowseLogDao;
import hygge.blog.domain.po.ArticleBrowseLog;
import hygge.blog.service.baidu.IPQueryServiceImpl;
import hygge.web.template.HyggeWebUtilContainer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Xavier
 * @date 2022/11/19
 */
@Slf4j
@Service
public class ArticleBrowseLogServiceImpl extends HyggeWebUtilContainer {
    @Autowired
    private ArticleBrowseLogDao articleBrowseLogDao;
    @Autowired
    private IPQueryServiceImpl ipQueryService;

    public void insertArticleBrowseLog(String aid, String title, String ip, Integer userId, String userAgent) {
        ArticleBrowseLog articleBrowseLog = ArticleBrowseLog.builder()
                .title(title)
                .ip(ip)
                .aid(aid)
                .userId(userId)
                .userAgent(userAgent)
                .build();

        articleBrowseLogDao.save(articleBrowseLog);
    }

    public void freshIpLocationBackgroundJob() {
        boolean continueFlag = true;

        while (continueFlag) {
            String targetIp = articleBrowseLogDao.findAnIpWithoutLocation();
            if (targetIp == null) {
                continueFlag = false;
            } else {
                // 尝试从本地解析 IP location
                String ipLocation = articleBrowseLogDao.findIpLocationFromLocal(targetIp);
                if (parameterHelper.isEmpty(ipLocation)) {
                    // 尝试从百度解析 IP location
                    ipLocation = ipQueryService.queryIpLocation(targetIp);
                    continueFlag = false;
                }

                if (parameterHelper.isNotEmpty(ipLocation)) {
                    articleBrowseLogDao.updateIpLocationForAll(targetIp, ipLocation);
                } else {
                    continueFlag = false;
                    log.error("解析 ipLocation({}) 失败.", targetIp);
                }
            }
        }
    }
}
