import * as React from 'react';
import {useContext} from 'react';
import {Header} from 'antd/es/layout/layout';
import {Button, Flex, Space, Spin, Switch, Tooltip} from 'antd';
import {MenuFoldOutlined, MenuUnfoldOutlined} from '@ant-design/icons';
import AppUserMenu from './AppUserMenu.tsx';
import {useIsMutating} from '@tanstack/react-query';
import {HomeContext} from '../context/HomeContext.tsx';
import Search from 'antd/es/input/Search';
import PropertiesHelper from '../../util/PropertiesHelper.ts';
import {HomeKeywordType} from '../../enums/EnumKeeper.ts';

const HomeHeaderStyle: React.CSSProperties = {
    padding: '0 2rem 0 0',
    top: 0,
    position: 'sticky',
    zIndex: 1,
    width: '100%',
    background: '#001529',
    color: '#fff'
}

export default function HomeHeader() {
    // 全局的 Pending 检测，如果单独则如 signIn.isPending 即可
    const isAnyPending = useIsMutating() > 0;
    const {
        collapsed, setCollapsed,
        keyword, setKeyword,
        keywordType, setKeywordType,
        searchParams, setSearchParams,
        fuzzySearch
    } = useContext(HomeContext);

    return (
        <Header style={HomeHeaderStyle}>
            <Flex justify={'space-between'} style={{height: '100%'}}>
                <Flex className={'Header-Left'} justify={'flex-start'} style={{width: '50%', alignItems: 'center'}}>
                    <Tooltip placement='bottom' title={collapsed ? '展开' : '收起'}>
                        <Button
                            type='text'
                            icon={collapsed ? <MenuUnfoldOutlined/> : <MenuFoldOutlined/>}
                            onClick={() => setCollapsed(!collapsed)}
                            style={{
                                color: '#FFF',
                                fontSize: '1.5rem',
                                width: '4rem',
                                height: '4rem',
                            }}
                        />
                    </Tooltip>
                    <Spin spinning={isAnyPending} size='large' style={{display: 'flex'}}/>
                </Flex>
                <Flex className={'Header-Right'} justify={'flex-end'} style={{width: '50%', alignItems: 'center'}}>
                    <Space size={'medium'}>
                        <Tooltip placement='bottom' title={'搜索类型'}>
                            <Switch checkedChildren='文章' unCheckedChildren='句子' value={keywordType == HomeKeywordType.POST} onChange={(value) => {
                                if (value) {
                                    setKeywordType(HomeKeywordType.POST);
                                } else {
                                    setKeywordType(HomeKeywordType.QUOTE);
                                }
                            }}/>
                        </Tooltip>
                        <Search style={{maxWidth: '20rem'}} placeholder='搜索关键字'
                                enterButton
                                size='middle'
                                value={keyword}
                                onChange={(event) => {
                                    const nextKeyword = event.target.value;
                                    // 复制一个新的 URLSearchParams 对象
                                    const nextParams = new URLSearchParams(searchParams);
                                    if (PropertiesHelper.isStringNotEmpty(nextKeyword)) {
                                        setKeyword(nextKeyword);
                                        nextParams.set('keyword', nextKeyword);
                                    } else {
                                        setKeyword(undefined);
                                        nextParams.delete('keyword');
                                    }
                                    setSearchParams(nextParams);
                                }}

                                onSearch={(value) => {
                                    fuzzySearch();
                                }}
                        />
                        <AppUserMenu/>
                    </Space>
                </Flex>
            </Flex>
        </Header>
    );
}
