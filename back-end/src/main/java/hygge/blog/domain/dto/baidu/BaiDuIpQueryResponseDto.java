package hygge.blog.domain.dto.baidu;

import hygge.blog.domain.dto.baidu.inner.BaiDuIpQueryResponseItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * @author Xavier
 * @date 2022/11/19
 */
@Getter
@Setter
@Builder
@Generated
@NoArgsConstructor
@AllArgsConstructor
public class BaiDuIpQueryResponseDto {
    private List<BaiDuIpQueryResponseItem> data;
}
