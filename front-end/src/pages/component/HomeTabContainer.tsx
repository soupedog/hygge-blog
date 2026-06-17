import {useContext, useMemo} from 'react';
import {Badge, Tabs, type TabsProps} from 'antd';
import {HomeContext} from '../context/HomeContext.tsx';
import HomeTabPostContent from './HomeTabPostContent.tsx';
import HomeTabQuoteContent from './HomeTabQuoteContent.tsx';
import HomeTabSearchContent from './HomeTabSearchContent.tsx';

export default function HomeTabContainer() {
    const {
        activeTap, setActiveTap,
        setCategoryCollapsed,
        categoryInfoMap,
        currentCategoryInfo, setCurrentCategoryInfo,
        firstTopicInitResult,
        searchResult,
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
                    <Badge key={`tabContentBadge_${item.topicInfo.tid}`} count={item.totalCount} overflowCount={9999} offset={[10, -20]}/>
                </>
            ),
            children: <HomeTabPostContent key={`tabContent_${item.topicInfo.tid}`} tid={item.topicInfo.tid} initData={activeTap == item.topicInfo.tid ? firstTopicInitResult : undefined}/>,
        }));

        return [
            ...dynamicTabs,
            {
                key: '句子收藏',
                label: (
                    <>
                        句子收藏
                        <Badge key={`tabContentBadge_句子收藏`} count={quoteInfo.totalCount} overflowCount={9999} offset={[10, -20]}/>
                    </>
                ),
                children: <HomeTabQuoteContent key={`tabContent_句子收藏`}/>,
            },
            {
                key: '搜索结果',
                label: (
                    <>
                        搜索结果
                        <Badge key={`tabContentBadge_搜索结果`} count={searchResult.totalCount} overflowCount={9999} offset={[10, -20]}/>
                    </>
                ),
                children: <HomeTabSearchContent key={`tabContent_搜索结果`}/>,
            },
            {
                key: '公告',
                label: (
                    <>
                        公告
                        <Badge key={`tabContentBadge_公告`} count={announcementInfoList.length} overflowCount={9999} offset={[10, -20]}/>
                    </>
                ),
                children: <div>公告内容</div>,
            },
        ];
    }, [topicOverviewInfoList, JSON.stringify(quoteInfo), JSON.stringify(searchResult), announcementInfoList.length]);

    return (
        <Tabs
            activeKey={activeTap}
            size={'large'}
            type={'card'}
            style={{marginTop: '1rem', padding: '1rem', borderRadius: '0.5rem', boxShadow: '0 4px 8px 0 rgba(0, 0, 0, 0.2)', backgroundColor: '#FFF'}}
            items={items}
            onChange={(key) => {
                setActiveTap(key);
                const listTemp = categoryInfoMap.get(key);
                if (listTemp) {
                    setCurrentCategoryInfo(listTemp);
                }

                if (key == '句子收藏') {
                    setCategoryCollapsed(true);
                }
                // 默认 key 是 tid，32 位
                if (key.length > 10) {
                    setCategoryCollapsed(false);
                }
            }}
        />
    );
}