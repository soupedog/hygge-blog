package hygge.blog.service.client;

import com.fasterxml.jackson.core.type.TypeReference;
import hygge.blog.domain.baidu.BaiduGatewayDto;
import hygge.blog.domain.baidu.inner.BaiduIpInfoDto;
import hygge.blog.domain.ipdatacloud.DataCloudGatewayDTO;
import hygge.web.util.http.bo.HttpResponse;
import hygge.web.util.http.impl.DefaultHttpHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author Xavier
 * @date 2022/11/19
 */
@Service
public class IPQueryClient {
    private static final TypeReference<BaiduGatewayDto<BaiduIpInfoDto>> typeReference = new TypeReference<>() {
    };
    private final DefaultHttpHelper httpHelper;
    @Value("${third-party.ipdatacloud.ipquery.key}")
    private String key;

    public IPQueryClient(DefaultHttpHelper httpHelper) {
        this.httpHelper = httpHelper;
    }

    /**
     * IP 数据云 "https://api.ipdatacloud.com/v2/query?ip=需要查询的ip&key=您申请的key" 的 IP 查询<br/>
     *
     * <pre>
     * {
     *      "code": 200,
     *      "data": {
     *          "location": {
     *              "city": "xx市",
     *              "country": "中国",
     *              "country_english": "China",
     *              "ip": " xx.xxx.xxx.xx ",
     *              "isp": "移动",
     *              "latitude": "xx.xxx",
     *              "longitude": "xxx.xxx",
     *              "province": "湖南"
     *           }
     *      },
     *      "msg": "success"
     * }
     * </pre>
     */
    public HttpResponse<Void, DataCloudGatewayDTO> queryIpLocation(String ip) {
        String url = UriComponentsBuilder
                .fromUriString("https://api.ipdatacloud.com/v2/query")
                .queryParam("ip", ip)
                .queryParam("key", key)
                .encode()
                .build()
                .toUriString();

        return httpHelper.get(url, DataCloudGatewayDTO.class);
    }

    /**
     * 百度企服 "https://qifu.baidu.com/" 中的 IP 查询(本服务由百度智能云和埃文科技联合提供)<br/>
     * 埃文科技：我们每天提供多达 3 个 IP 地址/域名的免费查询
     *
     * <pre>
     *     {
     *     "code":"Success",
     *     "data":{
     *         "continent":"亚洲",
     *         "country":"中国",
     *         "zipcode":"422400",
     *         "timezone":"UTC+8",
     *         "accuracy":"区县",
     *         "owner":"中国电信",
     *         "isp":"中国电信",
     *         "source":"数据挖掘",
     *         "areacode":"CN",
     *         "adcode":"430581",
     *         "asnumber":"4134",
     *         "lat":"26.783877",
     *         "lng":"110.734477",
     *         "radius":"37.1583",
     *         "prov":"湖南省",
     *         "city":"邵阳市",
     *         "district":"武冈市"
     *     },
     *     "charge":true,
     *     "msg":"查询成功",
     *     "ip":"223.155.54.93",
     *     "coordsys":"WGS84"
     * }
     * </pre>
     */
    public HttpResponse<Void, BaiduGatewayDto<BaiduIpInfoDto>> queryIpInfo(String ip) {
        String url = UriComponentsBuilder
                .fromUriString("https://qifu-api.baidubce.com/ip/geo/v1/district?ip={ip}")
                .encode()
                .build()
                .expand(ip)
                .toUriString();

        return httpHelper.get(url, typeReference);
    }

}
