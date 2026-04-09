package hygge.blog.service.local.normal;

import hygge.blog.domain.ipdatacloud.DataCloudGatewayDTO;
import hygge.blog.domain.ipdatacloud.inner.DataCloudIpInfoDTO;
import hygge.blog.domain.local.enums.BrowseLogTypeEnum;
import hygge.blog.domain.local.po.ArticleBrowseLog;
import hygge.blog.repository.database.ArticleBrowseLogDao;
import hygge.blog.service.client.IPQueryClient;
import hygge.util.template.HyggeJsonUtilContainer;
import hygge.web.util.http.bo.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Xavier
 * @date 2022/11/19
 */
@Slf4j
@Service
public class ArticleBrowseLogServiceImpl extends HyggeJsonUtilContainer {
    private final ArticleBrowseLogDao articleBrowseLogDao;
    private final IPQueryClient ipQueryService;
    private final static Pattern pattern_bot_baidu = Pattern.compile(BrowseLogTypeEnum.BOT_BAIDU.getValue(), Pattern.CASE_INSENSITIVE);
    private final static Pattern pattern_bot_google = Pattern.compile(BrowseLogTypeEnum.BOT_GOOGLE.getValue(), Pattern.CASE_INSENSITIVE);
    private final static Pattern pattern_bot_360 = Pattern.compile(BrowseLogTypeEnum.BOT_360.getValue(), Pattern.CASE_INSENSITIVE);
    private final static Pattern pattern_bot_byte = Pattern.compile(BrowseLogTypeEnum.BOT_Byte.getValue(), Pattern.CASE_INSENSITIVE);
    private final static Pattern pattern_bot_unknown = Pattern.compile(BrowseLogTypeEnum.BOT_UNKNOWN.getValue(), Pattern.CASE_INSENSITIVE);

    public ArticleBrowseLogServiceImpl(ArticleBrowseLogDao articleBrowseLogDao, IPQueryClient ipQueryService) {
        this.articleBrowseLogDao = articleBrowseLogDao;
        this.ipQueryService = ipQueryService;
    }

    public void insertArticleBrowseLog(String aid, String title, String ip, Integer userId, String userAgent) {
        ArticleBrowseLog articleBrowseLog = ArticleBrowseLog.builder()
                .title(title)
                .ip(ip)
                .aid(aid)
                .userId(userId)
                .userAgent(userAgent)
                .browseLogType(BrowseLogTypeEnum.UNCHECKED)
                .build();

        articleBrowseLogDao.save(articleBrowseLog);
    }

    public void insertArticleBrowseLogAsync(String aid, String title, String ip, Integer userId, String userAgent) {
        CompletableFuture.runAsync(() -> insertArticleBrowseLog(aid, title, ip, userId, userAgent))
                .exceptionally(e -> {
                    log.error("Fail to insert articleBrowseLog(%s-%s-%s-%d-%s).".formatted(aid, title, ip, userId, userAgent), e);
                    return null;
                });
    }

    public BrowseLogTypeEnum analysisBrowseLogTypeByUserAgent(String userAgent) {
        // 简单验证是否为人机
        Matcher matcher = pattern_bot_baidu.matcher(userAgent);
        if (matcher.find()) {
            return BrowseLogTypeEnum.BOT_BAIDU;
        }
        matcher = pattern_bot_google.matcher(userAgent);
        if (matcher.find()) {
            return BrowseLogTypeEnum.BOT_GOOGLE;
        }
        matcher = pattern_bot_360.matcher(userAgent);
        if (matcher.find()) {
            return BrowseLogTypeEnum.BOT_360;
        }
        matcher = pattern_bot_byte.matcher(userAgent);
        if (matcher.find()) {
            return BrowseLogTypeEnum.BOT_Byte;
        }
        matcher = pattern_bot_unknown.matcher(userAgent);
        if (matcher.find()) {
            return BrowseLogTypeEnum.BOT_UNKNOWN;
        }
        return BrowseLogTypeEnum.DEFAULT;
    }

    public void freshBrowseLogTypeBackgroundJob() {
        Example<ArticleBrowseLog> example = Example.of(ArticleBrowseLog.builder().browseLogType(BrowseLogTypeEnum.UNCHECKED).build());
        // 时间正排尝试取前 20 个
        PageRequest page = PageRequest.of(0, 20, Sort.by(Sort.Order.asc("createTs")));

        Page<ArticleBrowseLog> resultTemp = articleBrowseLogDao.findAll(example, page);
        List<ArticleBrowseLog> result = resultTemp.getContent();
        List<ArticleBrowseLog> needRefresh = new ArrayList<>(20);

        for (ArticleBrowseLog item : result) {
            BrowseLogTypeEnum oldType = item.getBrowseLogType();
            String userAgent = item.getUserAgent();
            BrowseLogTypeEnum newType = analysisBrowseLogTypeByUserAgent(userAgent);
            if (newType != null && newType.equals(oldType)) {
                log.warn("Analysis error ({}): please modify the analysis rules.", userAgent);
            } else {
                item.setBrowseLogType(newType);
                needRefresh.add(item);
            }
        }

        // 进行更新
        if (!needRefresh.isEmpty()) {
            articleBrowseLogDao.saveAll(needRefresh);
        }
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
                ArticleBrowseLog ipLocationInfoResult = articleBrowseLogDao.findIpLocationInfoFromLocal(targetIp);

                // 本地不存在相关 IP 信息时，尝试从第三方平台解析 IP location
                if (ipLocationInfoResult == null) {
                    HttpResponse<Void, DataCloudGatewayDTO> resultTemp = ipQueryService.queryIpLocation(targetIp);
                    if (resultTemp.isSuccess() &&
                            resultTemp.getData() != null && "success".equals(resultTemp.getData().getMsg())) {
                        DataCloudIpInfoDTO ipInfoDTO = resultTemp.getData().getData();

                        ipLocationInfoResult = ArticleBrowseLog.builder()
                                .ip(targetIp)
                                .ipLocation(ipInfoDTO.getLocation().toLocationInfo())
                                .latitude(ipInfoDTO.getLocation().getLatitude())
                                .longitude(ipInfoDTO.getLocation().getLongitude())
                                .build();
                    }
                    // 至少进行过一次远端查询了，终止查询
                    continueFlag = false;
                }

                if (ipLocationInfoResult != null) {
                    articleBrowseLogDao.updateIpLocationInfoForAll(targetIp, ipLocationInfoResult.getLatitude(), ipLocationInfoResult.getLongitude(), ipLocationInfoResult.getIpLocation(), currentTimeStamp);
                } else {
                    log.error("解析 ipLocation({}) 失败.", targetIp);
                }
            }
        }
    }
}
