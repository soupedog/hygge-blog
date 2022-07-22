package hygge.blog.domain.po.inner;

import hygge.blog.domain.enums.BackgroundMusicTypeEnum;
import hygge.blog.domain.enums.MediaPlayTypeEnum;
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
public class ArticleConfiguration {
    /**
     * 背景音乐类型:无背景音乐,默认类型(绝对路径),网易云外链
     */
    private BackgroundMusicTypeEnum backgroundMusicType;
    /**
     * 媒体播放模式:强制自动播放,强制非自动播放,建议自动播放(优先客户端本地配置),建议非自动播放(优先客户端本地配置)
     */
    private MediaPlayTypeEnum mediaPlayType;
    /**
     * 音乐源链接
     */
    private String src;
    /**
     * 音乐封面图片源链接</br>
     * 网易云音乐类型非必填
     */
    private String coverSrc;
    /**
     * 作品名称</br>
     * 网易云音乐类型非必填
     */
    private String name;
    /**
     * 作曲家信息</br>
     * 网易云音乐类型非必填
     */
    private String artist;
    /**
     * 歌词</br>
     * 网易云音乐类型非必填
     */
    private String lrc;
}
