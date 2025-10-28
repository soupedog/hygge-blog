import React, {useEffect} from 'react';
import {ConfigProvider, Layout} from "antd";
import zhCN from "antd/lib/locale/zh_CN";
import {Content, Header} from "antd/es/layout/layout";
import {class_index_title,} from "../component/properties/ElementNameContainer";
import HyggeFooter from "../component/HyggeFooter";

function FileManage() {

    useEffect(() => {
        // 依赖静态值表示仅初始化时调用一次
    }, []);

    return (
        <ConfigProvider locale={zhCN}>
            <Layout className="layout">
                <Header>
                    <div className={class_index_title + " floatToLeft"} style={{width: 200}}>我的小宅子---文件管理</div>
                </Header>
                <Content style={{padding: '0 50px', minHeight: window.innerHeight - 226}}>
                    test
                </Content>
                <HyggeFooter/>
            </Layout>
        </ConfigProvider>
    );
}

export default FileManage;