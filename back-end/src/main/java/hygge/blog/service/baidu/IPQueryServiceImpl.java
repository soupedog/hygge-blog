package hygge.blog.service.baidu;

import com.fasterxml.jackson.core.type.TypeReference;
import hygge.blog.domain.dto.baidu.BaiDuIpQueryResponseDto;
import hygge.blog.domain.dto.baidu.BaiduGatewayDto;
import hygge.blog.domain.dto.baidu.inner.BaiDuIpQueryResponseItem;
import hygge.blog.domain.dto.baidu.inner.BaiduIpInfoDto;
import hygge.web.util.http.bo.HttpResponse;
import hygge.web.util.http.impl.DefaultHttpHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

/**
 * @author Xavier
 * @date 2022/11/19
 */
@Service
public class IPQueryServiceImpl {
    private static final TypeReference<BaiduGatewayDto<BaiduIpInfoDto>> typeReference = new TypeReference<>() {
    };
    @Autowired
    private DefaultHttpHelper httpHelper;

    public String queryIpLocation(String ip) {
        String url = UriComponentsBuilder
                .fromUriString("https://sp1.baidu.com/8aQDcjqpAAV3otqbppnN2DJv/api.php?query={ip}&resource_id=5809")
                .encode()
                .build()
                .expand(ip)
                .toUriString();

        HttpResponse<Void, BaiDuIpQueryResponseDto> resultTemp = httpHelper.get(url, BaiDuIpQueryResponseDto.class);
        if (resultTemp.isSuccess()) {
            return Optional.ofNullable(resultTemp.getData())
                    .map(BaiDuIpQueryResponseDto::getData)
                    .map(list -> list.isEmpty() ? null : list.get(0))
                    .map(BaiDuIpQueryResponseItem::getLocation)
                    .orElse(null);
        }
        return null;
    }

    /**
     * 百度企服 "https://qifu.baidu.com/" 中的 IP 查询
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
