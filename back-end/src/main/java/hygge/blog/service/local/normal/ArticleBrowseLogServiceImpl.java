package hygge.blog.service.local.normal;

import hygge.blog.repository.database.ArticleBrowseLogDao;
import hygge.blog.domain.baidu.dto.BaiduGatewayDto;
import hygge.blog.domain.baidu.dto.inner.BaiduIpInfoDto;
import hygge.blog.domain.local.po.ArticleBrowseLog;
import hygge.blog.service.client.IPQueryClient;
import hygge.web.template.HyggeWebUtilContainer;
import hygge.web.util.http.bo.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

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
    private IPQueryClient ipQueryService;

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

    /**
     * 扫描并解析 IP 信息，至多进行一次远端查询就终止(防止使用频率过高，被远端 API 拉进黑名单)
     */
    public void freshIpLocationBackgroundJob() {
        boolean continueFlag = true;
        Timestamp currentTimeStamp = new Timestamp(System.currentTimeMillis());

        while (continueFlag) {
            String targetIp = articleBrowseLogDao.findAnIpWithoutLocation();
            if (targetIp == null) {
                // 不存在需要解析的 IP 了，终止
                continueFlag = false;
            } else {
                // 尝试从本地解析 IP location
                String ipLocation = articleBrowseLogDao.findIpLocationFromLocal(targetIp);

                // 本地不存在相关 IP 信息时，尝试从百度解析 IP location
                if (parameterHelper.isEmpty(ipLocation)) {
                    HttpResponse<Void, BaiduGatewayDto<BaiduIpInfoDto>> resultTemp = ipQueryService.queryIpInfo(targetIp);
                    if (resultTemp.isSuccess() &&
                            resultTemp.getData() != null && "Success".equals(resultTemp.getData().getCode())) {
                        BaiduIpInfoDto baiduIpInfoDto = resultTemp.getData().getData();

                        targetIp = baiduIpInfoDto.toLocationInfo();
                    }
                    // 至少进行过一次远端查询了，终止查询
                    continueFlag = false;
                }

                if (parameterHelper.isNotEmpty(ipLocation)) {
                    articleBrowseLogDao.updateIpLocationForAll(targetIp, ipLocation, currentTimeStamp);
                } else {
                    log.error("解析 ipLocation({}) 失败.", targetIp);
                }
            }
        }
    }
}
