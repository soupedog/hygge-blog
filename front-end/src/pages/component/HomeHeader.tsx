import * as React from 'react';
import {useContext} from 'react';
import {Header} from 'antd/es/layout/layout';
import {Button, Flex, message, Space, Spin, Switch, Tooltip} from 'antd';
import {MenuFoldOutlined, MenuUnfoldOutlined} from '@ant-design/icons';
import AppUserMenu from './AppUserMenu.tsx';
import {useIsMutating} from '@tanstack/react-query';
import {HomeContext} from '../context/HomeContext.tsx';
import Search from 'antd/es/input/Search';
import PropertiesHelper from '../../util/PropertiesHelper.ts';
import {HomeKeywordType} from '../../enums/EnumKeeper.ts';
import {appConfiguration} from '../../configuration/app.configuration.ts';

const headerZIndex = appConfiguration.toastDefaultZIndex - 10;

const HomeHeaderStyle: React.CSSProperties = {
    padding: '0 2rem 0 0',
    top: 0,
    position: 'sticky',
    zIndex: headerZIndex,
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
        searchResultCurrentPage, setSearchResultCurrentPage,
        searchResultPageSize, setSearchResultPageSize,
        setSearchCategoryInfo,
        setSearchResultOrderEnable,
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
                                    if (keyword != null) {
                                        setSearchResultOrderEnable(false);
                                        setSearchCategoryInfo(undefined);
                                        // TODO 分页信息非初始值，修改分页信息 HomeTabSearchContent 的 useEffect 会自动触发查询
                                        setSearchResultCurrentPage(1);
                                        setSearchResultPageSize(5);
                                        fuzzySearch({keyword: keyword, currentPage: 1, pageSize: 5});
                                    } else {
                                        message.warning({content: '搜索关键字不可为空！'});
                                    }
                                }}
                        />
                        <AppUserMenu/>
                    </Space>
                </Flex>
            </Flex>
        </Header>
    );
}
