package hygge.blog.domain.dto;

import hygge.blog.domain.enums.UserSexEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Xavier
 * @date 2022/7/17
 */
@Getter
@Setter
@Builder
@Generated
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "用户信息")
public class UserDto {
    @Schema(title = "用户编号", description = "系统自动生成用户编号", example = "U00000001")
    private String uid;
    @Schema(title = "密码", description = "用户密码", example = "xxxxxx")
    private String password;
    @Schema(title = "账号", description = "登录用的账号", example = "xxxxxx")
    private String userName;
    @Schema(title = "头像", description = "头像的完整链接", example = "https://xxxxxxx.com/666.jpg")
    private String userAvatar;
    @Schema(title = "性别", description = "用户填写的性别", example = "SECRET")
    private UserSexEnum userSex;
    @Schema(title = "简介", description = "用户个人简介", example = "系统免费赠送给每一份小可爱的简介")
    private String biography;
    @Schema(title = "出生日期", description = "出生日期的 UTC 毫秒级时间戳", example = "0")
    private Long birthday;
    @Schema(title = "手机号", description = "手机号(默认不公开)", example = "130xxxxxxxx")
    private String phone;
    @Schema(title = "邮箱", description = "邮箱(默认不公开)", example = "wwwasdasd@qq.com")
    private String email;
}
