package hygge.blog.service.baidu;

import hygge.blog.domain.dto.baidu.BaiDuIpQueryResponseDto;
import hygge.web.util.http.bo.HttpResponse;
import hygge.web.util.http.definition.HttpHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author Xavier
 * @date 2022/11/19
 */
@Service
public class IPQueryServiceImpl {
    @Autowired
    private HttpHelper httpHelper;

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

}
