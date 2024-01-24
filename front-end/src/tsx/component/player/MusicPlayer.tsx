import React, {useEffect} from 'react';
import {ArticleConfiguration} from "../../rest/ApiClient";
import {PropertiesHelper} from "../../util/UtilContainer";
// @ts-ignore
import APlayer from 'APlayer';

export interface MusicPlayerProps {
    configuration: ArticleConfiguration,
}

function MusicPlayer({configuration}: MusicPlayerProps) {
    useEffect(() => {
        if (configuration.backgroundMusicType == "DEFAULT") {
            let ap = new APlayer({
                lrcType: PropertiesHelper.isStringNotEmpty(configuration.lrc) ? 1 : null,
                container: document.getElementById('playerContainer'),
                audio: [{
                    autoplay: true,
                    name: configuration.name,
                    artist: configuration.artist,
                    url: configuration.src,
                    cover: configuration.coverSrc,
                    lrc: configuration.lrc
                }]
            });
        }
        // 依赖静态值表示仅初始化时调用一次
    }, []);

    switch (configuration.backgroundMusicType) {
        case "DEFAULT":
            return (
                <div id={"playerContainer"}/>
            );
        case "WANG_YI_YUN":
            return (
                <iframe width={"100%"} height={"105px"} src={configuration.src}/>
            )
        default:
            return null;
    }
}

export default MusicPlayer;