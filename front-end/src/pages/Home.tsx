import {useEffect} from 'react';
import {Card, Layout} from 'antd';

import AppFooter from './component/AppFooter.tsx';
import HomeSider from './component/HomeSider.tsx';
import {HomeProvider} from './context/HomeContext.tsx';
import {Content} from 'antd/es/layout/layout';
import HomeHeader from './component/HomeHeader.tsx';

export default function Home() {
    useEffect(() => {
        document.title = `我的小宅子`;
    }, []);

    return (
        <HomeProvider>
            <Layout>
                <HomeSider/>
                <Content>
                    <HomeHeader/>
                    <Card style={{marginTop: '4rem', minHeight: '2000px'}}>
                        主体
                    </Card>
                    <AppFooter/>
                </Content>
            </Layout>
        </HomeProvider>
    );
}
