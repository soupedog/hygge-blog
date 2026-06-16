import {useContext, useMemo} from 'react';
import {Badge, Tabs, type TabsProps} from 'antd';
import {HomeContext} from '../context/HomeContext.tsx';

export default function HomeTabs() {
    const {
        activeTap, setActiveTap,
        searchResultTotalCount,
        topicOverviewInfoList,
        quoteInfo,
        announcementInfoList,
    } = useContext(HomeContext);

    const items: TabsProps['items'] = useMemo(() => {
        const dynamicTabs = topicOverviewInfoList.map((item) => ({
            key: item.topicInfo.tid,
            label: (
                <>
                    {item.topicInfo.topicName}
                    <Badge count={item.totalCount} overflowCount={9999} offset={[10, -20]}/>
                </>
            ),
            children: <div>{item.topicInfo.topicName} 内容：</div>,
        }));

        return [
            ...dynamicTabs,
            {
                key: '句子收藏',
                label: (
                    <>
                        句子收藏
                        <Badge count={quoteInfo.totalCount} overflowCount={9999} offset={[10, -20]}/>
                    </>
                ),
                children: <div>句子收藏内容</div>,
            },
            {
                key: '搜索结果',
                label: (
                    <>
                        搜索结果
                        <Badge count={searchResultTotalCount} overflowCount={9999} offset={[10, -20]}/>
                    </>
                ),
                children: <div>搜索结果内容</div>,
            },
            {
                key: '公告',
                label: (
                    <>
                        公告
                        <Badge count={announcementInfoList.length} overflowCount={9999} offset={[10, -20]}/>
                    </>
                ),
                children: <div>公告内容</div>,
            },
        ];
    }, [topicOverviewInfoList, quoteInfo.totalCount, searchResultTotalCount, announcementInfoList.length]);

    return (
        <Tabs
            activeKey={activeTap}
            size={'large'}
            type={'card'}
            style={{marginTop: '1rem', padding: '1rem', borderRadius: '0.5rem', boxShadow: '0 4px 8px 0 rgba(0, 0, 0, 0.2)', backgroundColor: '#FFF'}}
            items={items}
            onChange={(key) => {
                setActiveTap(key);
            }}
        />
    );
}