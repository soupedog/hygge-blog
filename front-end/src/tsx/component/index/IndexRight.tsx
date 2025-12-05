import React from 'react';
import {Content} from "antd/es/layout/layout";
import {Layout} from "antd";
import IndexHeader from "./IndexHeader";
import HyggeFooter from "../HyggeFooter";
import CategoryCollapse from "./CategoryCollapse";
import IndexMainView from "./IndexMainView";

function IndexRight() {
    return (
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
                <CategoryCollapse/>
                <br/>
                <IndexMainView/>
            </Content>
            <HyggeFooter/>
        </Layout>
    );
}

export default IndexRight;