import {useContext, useEffect} from 'react';
import {Card, Layout} from 'antd';

import AppFooter from './component/AppFooter.tsx';
import HomeSider from './component/HomeSider.tsx';
import {HomeContext} from './context/HomeContext.tsx';
import {Content} from 'antd/es/layout/layout';
import HomeHeader from './component/HomeHeader.tsx';
import HomeCategoryCollapse from './component/HomeCategoryCollapse.tsx';
import {useHomeService} from '../util/ApiService.ts';
import HomeTabs from './component/HomeTabs.tsx';

export default function Home() {
    const {fetch} = useHomeService();

    const {
        setCategoryList,
        setActiveTap,
        setTopicOverviewInfoList,
        setQuoteInfo,
        setAnnouncementInfoList,
    } = useContext(HomeContext);

    useEffect(() => {
        document.title = `我的小宅子`;

        fetch.mutate(undefined, {
            onSuccess: (data) => {
                setTopicOverviewInfoList(data.topicOverviewInfoList);
                setCategoryList(data.topicOverviewInfoList[0]?.categoryListInfo);
                setActiveTap(data.topicOverviewInfoList[0].topicInfo.tid);
                setQuoteInfo(data.quoteInfo);
                setAnnouncementInfoList(data.announcementInfoList);
            }
        });
    }, []);

    return (
        <Layout>
            <HomeSider/>
            <Content style={{ backgroundColor: '#FFF'}}>
                <HomeHeader/>
                <Card variant="borderless" style={{minHeight: '2000px', margin: '0 2rem 0 2rem', backgroundColor: '#FFF'}}>
                    <HomeCategoryCollapse/>
                    <HomeTabs/>
                </Card>
                <AppFooter/>
            </Content>
        </Layout>
    );
}
