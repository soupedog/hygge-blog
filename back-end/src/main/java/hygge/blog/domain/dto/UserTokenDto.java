package hygge.blog.domain.dto;

import hygge.blog.domain.enums.TokenScopeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;

/**
 * @author Xavier
 * @date 2022/7/19
 */
@Data
@Builder
@Generated
@NoArgsConstructor
@AllArgsConstructor
public class UserTokenDto {
    private TokenScopeEnum scope;
    private String token;
    private String refreshKey;
    private Long deadline;
}
