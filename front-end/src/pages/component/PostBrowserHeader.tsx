import * as React from 'react';
import {useEffect, useState} from 'react';
import {Header} from 'antd/es/layout/layout';
import {Flex, Spin, Tooltip} from 'antd';
import clsx from 'clsx';
import {appConfiguration} from '../../configuration/app.configuration.ts';
import {RollbackOutlined} from '@ant-design/icons';
import UrlHelper from '../../util/UrlHelper.ts';
import AppUserMenu from './AppUserMenu.tsx';

const headerZIndex = appConfiguration.toastDefaultZIndex - 10;

export interface PostBrowserHeaderProps {
    readonly isAnyPending: boolean;
}

const PostBrowserHeaderSpinStyle: React.CSSProperties = {
    margin: '1rem 0 1rem 0',
}

export default function PostBrowserHeader({isAnyPending}: PostBrowserHeaderProps) {
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
            <Flex justify={'space-between'} style={{height: '100%'}}>
                <Flex className={'PostBrowserHeader-left'} justify={'flex-start'} style={{width: '50%'}}>
                    <Tooltip placement='bottom' title={'返回首页'}>
                        <RollbackOutlined onClick={() => {
                            UrlHelper.navigateTo({path: '/'});
                        }} style={{color: '#fff', fontWeight: 'bold', fontSize: '2rem', lineHeight: '4rem'}}/>
                    </Tooltip>
                </Flex>
                <Flex className={'PostBrowserHeader-right'} justify={'flex-end'} style={{width: '50%'}}>
                    <AppUserMenu/>
                    <Spin spinning={isAnyPending} size='large' style={PostBrowserHeaderSpinStyle}/>
                </Flex>
            </Flex>
        </Header>
    );
}
