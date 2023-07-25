package hygge.blog.domain.local.dto;

import hygge.blog.domain.local.enums.TokenScopeEnum;
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
    private UserDto user;
    private TokenScopeEnum scope;
    private String token;
    private String refreshKey;
    private Long deadline;
}
