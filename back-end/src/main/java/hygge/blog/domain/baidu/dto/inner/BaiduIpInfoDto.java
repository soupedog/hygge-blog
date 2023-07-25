package hygge.blog.domain.baidu.dto.inner;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Xavier
 * @date 2023/7/6
 * @since 1.0
 */
@Getter
@Setter
@Builder
@Generated
@NoArgsConstructor
@AllArgsConstructor
public class BaiduIpInfoDto {
    private String continent;
    private String country;
    private String zipcode;
    private String timezone;
    private String accuracy;
    private String owner;
    private String isp;
    private String source;
    private String areacode;
    private String adcode;
    private String asnumber;
    private String lat;
    private String lng;
    private String radius;
    private String prov;
    private String city;
    private String district;

    public String toLocationInfo() {
        if (prov.equals(city)) {
            // 直辖市
            return String.format("%s-%s-%s-%s", isp, country, city, district);
        } else {
            return String.format("%s-%s-%s-%s-%s", isp, country, prov, city, district);
        }
    }
}
