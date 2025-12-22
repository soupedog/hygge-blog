package hygge.blog.domain.ipdatacloud.inner;

import hygge.util.UtilCreator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashMap;

/**
 * @author Xavier
 * @date 2025/12/22
 */
@Getter
@Setter
@Builder
@Generated
@NoArgsConstructor
@AllArgsConstructor
public class DataCloudLocation {
    /**
     * 城市
     */
    private String city;
    /**
     * 国家
     */
    private String country;
    private String country_english;
    private String ip;
    /**
     * 运营商
     */
    private String isp;
    /**
     * 纬度
     */
    private String latitude;
    /**
     * 精度
     */
    private String longitude;
    /**
     * 省份
     */
    private String province;

    public String toLocationInfo() {
        if (province != null && !province.isEmpty() && city != null && !city.isEmpty()) {
            // 省市均非空
            if (province.equals(city)) {
                // 直辖市
                return String.format("%s-%s-%s", city, country, isp);
            } else {
                return String.format("%s-%s-%s-%s", province, city, country, isp);
            }
        } else {
            LinkedHashMap<String, String> map = new LinkedHashMap<>();
            if (province != null && !province.isEmpty()) {
                map.put("省份", province);
            }
            if (city != null && !city.isEmpty()) {
                map.put("城市", city);
            }
            if (country != null && !country.isEmpty()) {
                map.put("国家", country);
            }
            if (isp != null && !isp.isEmpty()) {
                map.put("运营商", isp);
            }

            if (!map.isEmpty()) {
                return UtilCreator.INSTANCE.getDefaultJsonHelperInstance(false).formatAsString(map);
            } else {
                return "查询结果为空";
            }
        }
    }
}
