import {useEffect} from 'react';
import {Layout} from 'antd';
import AppFooter from './component/AppFooter.tsx';

export default function Home() {
    useEffect(() => {
    }, []);

    return (
        <Layout>
            <div>
                主页
            </div>
            <AppFooter/>
        </Layout>
    );
}
