import React from 'react';
import {IndexContext} from '../../page/Index';
import {Content} from "antd/es/layout/layout";
import {Layout} from "antd";
import IndexHeader from "./IndexHeader";
import HyggeFooter from "../HyggeFooter";
import CategoryContainer from "./CategoryContainer";
import IndexMainView from "./IndexMainView";

function IndexRight() {
    return (
        <IndexContext.Consumer>
            {({menuFolded, updateMenuFolded}) => (
                <Layout className="right_box site-layout">
                    <IndexHeader/>
                    <Content
                        style={{
                            borderRadius: "15px",
                            background: "#fff",
                            margin: '24px 60px',
                            marginTop: "100px",
                            padding: "24px",
                            minHeight: window.innerHeight - 284 + "px",
                        }}
                    >
                        <CategoryContainer/>
                        <br/>
                        <IndexMainView/>
                    </Content>
                    <HyggeFooter/>
                </Layout>
            )}
        </IndexContext.Consumer>
    );
}

export default IndexRight;