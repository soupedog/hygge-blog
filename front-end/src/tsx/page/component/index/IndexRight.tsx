import * as React from "react"

import {Collapse, Input, Layout, Space, Spin, Switch, Tabs} from 'antd';
import {MenuFoldOutlined, MenuUnfoldOutlined} from '@ant-design/icons';
import {IndexContainerContext} from "../../context/HyggeContext";
import {IndexContainerStatus} from "../../IndexContainer";
import {HyggeFooter} from "../HyggeFooter";
import clsx from "clsx";
import {LogHelper} from '../../../utils/UtilContainer';

const {Header, Sider, Content} = Layout;
const {Panel} = Collapse;
const {TabPane} = Tabs;
const {Search} = Input;

// 描述该组件 props 数据类型
export interface IndexRightProps {
}

// 描述该组件 states 数据类型
export interface IndexRightStatus {
}

export class IndexRight extends React.Component<IndexRightProps, IndexRightStatus> {
    constructor(props: IndexRightProps) {
        super(props);
        this.state = {};
        LogHelper.info({className: "IndexRight", msg: "初始化成功"});
    }

    render() {
        return (
            <IndexContainerContext.Consumer>
                {(status: IndexContainerStatus) => (
                    <>
                        <Layout className={"right_box"}>
                            <Sider collapsed={status.folded} key={"placeholder"}>{/*IndexLeft 的占位区块*/}</Sider>
                            <Layout>
                                <Header className={"header_menu floatToToLeft"}>
                                    {React.createElement(status.folded ? MenuUnfoldOutlined : MenuFoldOutlined, {
                                        className: 'indexMenuSwitch',
                                        onClick: () => {
                                            status.updateRootStatus!({folded: !status.folded});
                                        },
                                    })}
                                    <div className={clsx({
                                        "floatToRight": true,
                                        "headMenuSmallMode": !status.folded,
                                        "headMenuBigMode": status.folded
                                    })}>
                                        <div className={"floatToRight"} style={{marginLeft: "40px"}}>
                                            <Space size="middle">
                                                <Spin spinning={status.netWorkArrayCounter!.length > 0} size="large"/>
                                            </Space>
                                        </div>
                                        <div className={"floatToRight"}>
                                            {/*<HeaderUserInfoBox userInfo={currentUser} key={"HeaderUserInfoBox"}/>*/}
                                        </div>
                                        <div className={"floatToRight"} style={{padding: "18px"}}>
                                            <Search placeholder="搜索关键字"
                                                    allowClear
                                                    enterButton
                                                    size="middle"
                                                    onSearch={(value) => {
                                                    }}
                                            />
                                        </div>
                                        <div id={"searchModeSwitch"} className={"floatToRight"}>
                                            <Switch checkedChildren="搜索模式：文章" unCheckedChildren="搜索模式：收藏"
                                                    defaultChecked/>
                                        </div>
                                    </div>
                                    <div className="clearfix"></div>
                                </Header>
                                <Content
                                    className={"site-layout-background myContent"}
                                    style={{
                                        margin: '88px 16px',
                                        padding: 24,
                                        minHeight: 800,
                                    }}
                                ></Content>
                                <HyggeFooter/>
                            </Layout>
                        </Layout>
                    </>
                )}
            </IndexContainerContext.Consumer>
        );
    }


}