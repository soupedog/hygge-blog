import * as React from 'react';
import {useEffect, useState} from 'react';
import {Header} from 'antd/es/layout/layout';
import {Spin} from 'antd';
import clsx from 'clsx';
import {appConfiguration} from '../../configuration/app.configuration.ts';

const headerZIndex = appConfiguration.toastDefaultZIndex - 1;

export interface PostBrowserHeaderProps {
    readonly title: string;
    readonly isAnyPending: boolean;
}

const PostBrowserHeaderTitleStyle: React.CSSProperties = {
    float: 'left',
    width: '6.25rem',
    height: '2rem',
    margin: '1rem',
    background: 'rgba(255, 255, 255, 0.3)',
    fontSize: '1.125rem',
    textAlign: 'center',
    fontWeight: 'bold',
    lineHeight: '2rem',
    color: 'white',
    borderRadius: '0.25rem'
}

const PostBrowserHeaderSpinStyle: React.CSSProperties = {
    float: 'right',
    margin: '1rem 0 1rem 0',
}

export default function PostBrowserHeader({title, isAnyPending}: PostBrowserHeaderProps) {
    const [isTransparent, setIsTransparent] = useState(true);

    useEffect(() => {
        // 滚动处理函数
        const handleScroll = () => {
            // 获取当前滚动条垂直位置
            const scrollTop = window.pageYOffset;
            // 当滚动距离 336 时，设为非透明状态
            setIsTransparent(scrollTop <= 336);
        };

        // 添加滚动事件监听
        window.addEventListener('scroll', handleScroll);

        // 组件卸载时，记得移除事件监听
        return () => {
            window.removeEventListener('scroll', handleScroll);
        };
    }, []);

    return (
        <Header className={clsx({
            'backgroundTransparent': isTransparent
        })} style={{position: 'fixed', zIndex: headerZIndex, width: '100%'}}>
            <div style={PostBrowserHeaderTitleStyle}>{title}</div>
            <Spin spinning={isAnyPending} size='large' style={PostBrowserHeaderSpinStyle}/>
        </Header>
    );
}
