import * as React from 'react';
import {useEffect} from 'react';
import {Header} from 'antd/es/layout/layout';
import {Flex, Space, Spin} from 'antd';
import AppUserMenu from './AppUserMenu.tsx';

export interface AppBaseHeaderProps {
    readonly title: string;
    readonly isAnyPending: boolean;
}

export default function AppBaseHeader({title, isAnyPending}: AppBaseHeaderProps) {

    useEffect(() => {
        // 依赖静态值表示仅初始化时调用一次
    }, []);

    return (
        <Header>
            <Flex justify={'space-between'} style={{height: '100%'}}>
                <Flex className={'hygge-header-Left'} justify={'flex-start'} style={{width: '50%', alignItems: 'center'}}>
                    <div className={'hygge-header-title'}>{title}</div>
                    <Spin spinning={isAnyPending} size='large' style={{display: 'flex'}}/>
                </Flex>
                <Flex className={'hygge-header-right'} justify={'flex-end'} style={{width: '50%', alignItems: 'center'}}>
                    <Space size={'medium'}>
                        <AppUserMenu/>
                    </Space>
                </Flex>
            </Flex>
        </Header>
    );
}
