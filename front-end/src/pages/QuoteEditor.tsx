import {useEffect} from 'react';
import {Layout} from 'antd';
import AppBaseHeader from './component/AppBaseHeader.tsx';
import AppFooter from './component/AppFooter.tsx';
import {Content} from 'antd/es/layout/layout';

export default function QuoteEditor() {

    useEffect(() => {
        // 依赖静态值表示仅初始化时调用一次
    }, []);

    return (
        <Layout className={'full-screen-min-y'}>
            <AppBaseHeader title={'句子收藏编辑'} isAnyPending={true}/>
            <Content>
            </Content>
            <AppFooter/>
        </Layout>
    );
}
