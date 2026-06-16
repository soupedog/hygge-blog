import {useContext, useEffect} from 'react';
import {Card, Layout} from 'antd';

import AppFooter from './component/AppFooter.tsx';
import HomeSider from './component/HomeSider.tsx';
import {HomeContext} from './context/HomeContext.tsx';
import {Content} from 'antd/es/layout/layout';
import HomeHeader from './component/HomeHeader.tsx';
import HomeCategoryCollapse from './component/HomeCategoryCollapse.tsx';
import {useHomeService} from '../util/ApiService.ts';

export default function Home() {
    const {fetch} = useHomeService();

    const {
        setCategoryList
    } = useContext(HomeContext);

    useEffect(() => {
        document.title = `我的小宅子`;

        fetch.mutate(undefined, {
            onSuccess: (data) => {
                setCategoryList(data.topicOverviewInfoList[0].categoryListInfo);
            }
        });
    }, []);

    return (
        <Layout>
            <HomeSider/>
            <Content>
                <HomeHeader/>
                <Card style={{minHeight: '2000px'}}>
                    <HomeCategoryCollapse/>
                </Card>
                <AppFooter/>
            </Content>
        </Layout>
    );
}
