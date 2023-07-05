package hygge.blog.service.baidu;

import com.fasterxml.jackson.core.type.TypeReference;
import hygge.blog.domain.dto.baidu.BaiDuIpQueryResponseDto;
import hygge.blog.domain.dto.baidu.BaiduGatewayDto;
import hygge.blog.domain.dto.baidu.inner.BaiduIpInfoDto;
import hygge.web.util.http.bo.HttpResponse;
import hygge.web.util.http.impl.DefaultHttpHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author Xavier
 * @date 2022/11/19
 */
@Service
public class IPQueryServiceImpl {
    private static final TypeReference<BaiduGatewayDto<BaiduIpInfoDto>> typeReference = new TypeReference<BaiduGatewayDto<BaiduIpInfoDto>>() {
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
        if (resultTemp.isSuccess() &&
                resultTemp.getData() != null && resultTemp.getData().getData() != null && !resultTemp.getData().getData().isEmpty()) {
            return resultTemp.getData().getData().get(0).getLocation();
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
    public String queryIpInfo(String ip) {
        String url = UriComponentsBuilder
                .fromUriString("https://qifu-api.baidubce.com/ip/geo/v1/district?ip={ip}")
                .encode()
                .build()
                .expand(ip)
                .toUriString();

        HttpResponse<Void, BaiduGatewayDto<BaiduIpInfoDto>> resultTemp = httpHelper.get(url, typeReference);
        if (resultTemp.isSuccess() &&
                resultTemp.getData() != null && "Success".equals(resultTemp.getData().getCode())) {

            BaiduIpInfoDto baiduIpInfoDto = resultTemp.getData().getData();

            if (baiduIpInfoDto.getProv().equals(baiduIpInfoDto.getCity())) {
                // 直辖市
                return baiduIpInfoDto.getCountry() + "-" + baiduIpInfoDto.getCity() + "-" + baiduIpInfoDto.getDistrict();
            } else {
                return baiduIpInfoDto.getCountry() + "-" + baiduIpInfoDto.getProv() + "-" + baiduIpInfoDto.getCity() + "-" + baiduIpInfoDto.getDistrict();
            }
        }
        return null;
    }

}
