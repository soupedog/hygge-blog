import React from 'react';
import {IndexContext} from '../../page/Index';
import {Content} from "antd/es/layout/layout";
import {Layout} from "antd";
import IndexHeader from "./IndexHeader";
import HyggeFooter from "../HyggeFooter";

function IndexRight() {
    return (
        <IndexContext.Consumer>
            {({menuFolded, updateMenuFolded}) => (
                <Layout className="right_box site-layout">
                    <IndexHeader/>
                    <Content
                        style={{
                            borderRadius: 15,
                            background: "#fff",
                            margin: '24px 60px',
                            marginTop: 100,
                            padding: 24,
                            minHeight: window.innerHeight - 282,
                        }}
                    >
                    </Content>
                    <HyggeFooter/>
                </Layout>
            )}
        </IndexContext.Consumer>
    );
}

export default IndexRight;