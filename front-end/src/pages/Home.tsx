import {useContext, useEffect} from 'react';
import {Card, Layout} from 'antd';

import AppFooter from './component/AppFooter.tsx';
import HomeSider from './component/HomeSider.tsx';
import {HomeContext} from './context/HomeContext.tsx';
import {Content} from 'antd/es/layout/layout';
import HomeHeader from './component/HomeHeader.tsx';
import HomeCategoryCollapse from './component/HomeCategoryCollapse.tsx';
import {useHomeService} from '../util/ApiService.ts';
import HomeTabContainer from './component/HomeTabContainer.tsx';

export default function Home() {
    const {fetch} = useHomeService();

    const {
        setTopicOverviewInfoList,
        addCategoryInfoOfTopic,
        setCurrentCategoryInfo,
        setFirstTopicInitResult,
        setActiveTap,
        setQuoteInfo,
        setAnnouncementInfoList,
    } = useContext(HomeContext);

    useEffect(() => {
        document.title = `我的小宅子`;

        fetch.mutate(undefined, {
            onSuccess: (data) => {
                data.topicOverviewInfoList.map(item => {
                    addCategoryInfoOfTopic({tid: item.topicInfo.tid, list: item.categoryListInfo});
                });

                setFirstTopicInitResult({
                    dataSet: data.articleSummaryInfo.articleSummaryList,
                    totalCount: data.articleSummaryInfo.totalCount
                });

                setActiveTap(data.topicOverviewInfoList[0]?.topicInfo.tid);
                const listTemp = data.topicOverviewInfoList[0]?.categoryListInfo;
                if (listTemp) {
                    setCurrentCategoryInfo(listTemp);
                }

                setQuoteInfo(data.quoteInfo);
                setAnnouncementInfoList(data.announcementInfoList);
                setTopicOverviewInfoList(data.topicOverviewInfoList);
            }
        });
    }, []);

    return (
        <Layout>
            <HomeSider/>
            <Content className={'full-screen-min-y'} style={{backgroundColor: '#FFF'}}>
                <HomeHeader/>
                <Card style={{margin: '0 2rem 2rem 2rem', backgroundColor: '#FFF'}}>
                    <HomeCategoryCollapse/>
                    <HomeTabContainer/>
                </Card>
                <AppFooter/>
            </Content>
        </Layout>
    );
}
