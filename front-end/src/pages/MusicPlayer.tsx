import APlayer from 'aplayer-ts'
import 'aplayer-ts/src/css/base.css'
import {useEffect, useRef} from "react";

function MusicPlayer() {
    // playerRef.current 是永久变量，这样才能在方法外被引用
    const playerRef = useRef<APlayer>(null);

    useEffect(() => {
        // 依赖静态值表示仅初始化时调用一次
        document.title = "音乐播放器";

        // 初始化播放器
        playerRef.current = APlayer().init({
            container: document.getElementById('playerContainer')!,
            audio: [{
                name: '歌曲名称',
                artist: '歌手名称',
                url: 'song.mp3',
                cover: 'cover.jpg'
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
    });

    return (
        <div id={"playerContainer"}/>
    )
}

export default MusicPlayer;