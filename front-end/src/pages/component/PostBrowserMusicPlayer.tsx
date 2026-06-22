import {useEffect, useRef} from 'react';
import type {ArticleConfiguration} from '../../util/ApiClient.ts';
import APlayer from 'aplayer-ts';
import 'aplayer-ts/src/css/base.css'
import PropertiesHelper from '../../util/PropertiesHelper.ts';

export interface MusicPlayerProps {
    configuration: ArticleConfiguration;
}

export default function PostBrowserMusicPlayer({configuration}: MusicPlayerProps) {
    // playerRef.current 是永久变量，这样才能在方法外被引用
    const playerRef = useRef<APlayer>(null);

    useEffect(() => {
        // 网易云等外源播放器不需要本地初始化
        if (configuration.backgroundMusicType == 'DEFAULT') {
            // 初始化播放器
            playerRef.current = APlayer().init({
                container: document.getElementById('playerContainer')!,
                lrcType: PropertiesHelper.isStringNotEmpty(configuration.lrc) ? 1 : undefined,
                audio: [{
                    name: configuration.name,
                    artist: configuration.artist,
                    url: configuration.src,
                    cover: configuration.coverSrc,
                    lrc: configuration.lrc
                }],
                volume: 1,        // 音量 0-1
                theme: '#b7daff'    // 主题色
            });

            // 返回值可以理解为当做析构函数用，清扫初始化后的负作用
            // 常见有负作用的事物：
            // 定时器：clearInterval
            // 事件监听：removeEventListener
            // 播放器：destroy()
            // WebSocket：close()
            return () => {
                // 播放器对象存在浏览器的内存中，和 React 组件是两个世界，不会自动跟随 React 组件销毁而销毁
                // useEffect 可以多次定义多次，原则上是从上到下顺序
                playerRef.current?.destroy();
                // 主动指向 null，给垃圾回收提供帮助
                playerRef.current = null;
            };
        }
    }, []);

    switch (configuration.backgroundMusicType) {
        case 'DEFAULT':
            return (
                <div id={'playerContainer'}/>
            );
        case 'WANG_YI_YUN':
            return (
                <iframe width={'100%'} height={'6rem'} src={configuration.src}/>
            )
        default:
            return null;
    }
}
