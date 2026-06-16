import {useContext} from 'react';
import {Badge, Tabs, type TabsProps} from 'antd';
import {HomeContext} from '../context/HomeContext.tsx';
import type {TopicOverviewInfo} from '../../util/ApiClient.ts';

export default function HomeTabs() {
    const {
        activeTap, setActiveTap,
        searchResultTotalCount,
        topicOverviewInfoList,
        quoteInfo,
        announcementInfoList,
    } = useContext(HomeContext);

    return (
        <Tabs activeKey={activeTap} size={'large'} type={'card'} style={{marginTop: '1rem', padding: '1rem', borderRadius: '0.5rem', boxShadow: '0 4px 8px 0 rgba(0, 0, 0, 0.2)', backgroundColor: '#FFF'}}
            // @ts-ignore
              items={buildItems(topicOverviewInfoList)}
              onChange={(key) => {
                  setActiveTap(key);
              }}
        />
    );

    function buildItems(list: Array<TopicOverviewInfo>) {
        const items = new Array<TabsProps>();
        topicOverviewInfoList.map((item) => {
            items.push(
                {
                    key: item.topicInfo.tid,
                    label: (
                        <>
                            {item.topicInfo.topicName}
                            <Badge count={item.totalCount} overflowCount={9999} offset={[10, -20]}/>
                        </>
                    ),
                    children: <div>{item.topicInfo.topicName} 内容：</div>,
                } as TabsProps
            );
        });

        items.push(
            {
                key: '句子收藏',
                label: (
                    <>
                        句子收藏
                        <Badge count={quoteInfo.totalCount} overflowCount={9999} offset={[10, -20]}/>
                    </>
                ),
                children: <div></div>,
            } as TabsProps
        );

        items.push(
            {
                key: '搜索结果',
                label: (
                    <>
                        搜索结果
                        <Badge
                            count={searchResultTotalCount}
                            overflowCount={9999}
                            offset={[10, -20]}/>
                    </>
                ),
                children: <div></div>,
            } as TabsProps
        );

        items.push(
            {
                key: '公告',
                label: (
                    <>
                        公告
                        <Badge count={announcementInfoList.length} overflowCount={9999}
                               offset={[10, -20]}/>
                    </>
                ),
                children: <div></div>,
            } as TabsProps
        );
        return items;
    }
}
