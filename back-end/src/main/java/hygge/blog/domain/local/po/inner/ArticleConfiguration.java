package hygge.blog.domain.local.po.inner;

import hygge.blog.domain.local.enums.BackgroundMusicTypeEnum;
import hygge.blog.domain.local.enums.MediaPlayTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Xavier
 * @date 2022/7/21
 */
@Getter
@Setter
@Builder
@Generated
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "文章配置项信息")
public class ArticleConfiguration {
    @Schema(title = "背景音乐类型", description = "无背景音乐,默认类型(绝对路径),网易云外链")
    private BackgroundMusicTypeEnum backgroundMusicType;
    @Schema(title = "媒体播放模式", description = "强制自动播放,强制非自动播放,建议自动播放(优先客户端本地配置),建议非自动播放(优先客户端本地配置)")
    private MediaPlayTypeEnum mediaPlayType;
    @Schema(title = "音乐源链接")
    private String src;
    @Schema(title = "音乐封面图片源链接", description = "网易云音乐类型非必填")
    private String coverSrc;
    @Schema(title = "作品名称", description = "网易云音乐类型非必填")
    private String name;
    @Schema(title = "作曲家信息", description = "网易云音乐类型非必填")
    private String artist;
    @Schema(title = "歌词", description = "网易云音乐类型非必填")
    private String lrc;
}
