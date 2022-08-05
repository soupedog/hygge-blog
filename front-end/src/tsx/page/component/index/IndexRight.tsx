import * as React from "react"

import {Badge, Button, Col, Collapse, Input, Layout, Row, Spin, Switch, Tabs, Timeline, Tooltip} from 'antd';
import {MenuFoldOutlined, MenuUnfoldOutlined} from '@ant-design/icons';
import {IndexContainerContext} from "../../context/HyggeContext";
import {IndexContainerStatus, SearchType} from "../../IndexContainer";
import clsx from "clsx";
import {LogHelper, UrlHelper} from '../../../utils/UtilContainer';
import {HyggeFooter} from "../HyggeFooter";

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
                    <Layout className="right_box site-layout">
                        <Header className="site-layout-background"
                                style={{
                                    padding: 0,
                                    position: 'fixed',
                                    zIndex: 1,
                                    width: '100%',
                                    background: "#001529",
                                    color: "#fff"
                                }}>
                            <Row gutter={[0, 0]} justify="start" className={clsx({
                                "headMenuSmallMode": !status.folded,
                                "headMenuBigMode": status.folded
                            })}>
                                <Col md={2} xl={12}>
                                    <Tooltip placement="bottom" title={status.folded ? "展开" : "收起"}>
                                        {React.createElement(status.folded ? MenuUnfoldOutlined : MenuFoldOutlined, {
                                            className: 'trigger',
                                            onClick: () => status.updateRootStatus!({folded: !status.folded}),
                                        })}
                                    </Tooltip>
                                </Col>
                                <Col md={22} xl={12}>
                                    <Row gutter={[0, 0]} justify="end">
                                        <Col md={1} xl={4}>{/*占位符*/}</Col>
                                        <Col md={3} xl={3}>
                                            <Tooltip placement="bottom" title={"搜索类型"}>
                                                <Switch checkedChildren="文章" unCheckedChildren="句子" defaultChecked/>
                                            </Tooltip>
                                        </Col>
                                        <Col md={8} xl={11}>
                                            <Search style={{marginTop: 16}} placeholder="搜索关键字"
                                                    allowClear
                                                    enterButton
                                                    size="middle"
                                                    onSearch={(value) => {
                                                        let searchType: SearchType;
                                                        if (document.querySelector("#searchModeSwitch")!.querySelector("button")!.ariaChecked == "true") {
                                                            searchType = SearchType.ARTICLE;
                                                        } else {
                                                            searchType = SearchType.QUOTE;
                                                        }
                                                        console.log(searchType)
                                                    }}
                                            />
                                        </Col>
                                        <Col md={1} xl={1}>{/*占位符*/}</Col>
                                        <Col md={3} xl={3}>
                                            <Button type="primary"
                                                    onClick={() => UrlHelper.openNewPage({
                                                        path: "#/signin",
                                                        inNewTab: false
                                                    })}>登录</Button>
                                        </Col>
                                        <Col md={2} xl={2} className={"textCenter"}>
                                            <Spin spinning/>
                                        </Col>
                                    </Row>
                                </Col>
                            </Row>
                        </Header>
                        <Content
                            id={"myContent"}
                            className="site-layout-background"
                            style={{
                                borderRadius: 15,
                                background: "#fff",
                                margin: '24px 16px',
                                marginTop: 100,
                                padding: 24,
                                minHeight: window.innerHeight - 282,
                            }}
                        >
                            <Tabs defaultActiveKey="编程" type="card" size={"large"}>
                                <TabPane
                                    tab={
                                        <>
                                            <span>编程</span>
                                            <Badge count={66} overflowCount={9999} offset={[10, -10]}></Badge>
                                        </>
                                    }
                                    key="编程"
                                >
                                    Tab 1
                                </TabPane>
                                <TabPane
                                    tab={
                                        <>
                                            <span>杂项</span>
                                            <Badge count={5} overflowCount={9999} offset={[10, -10]}></Badge>
                                        </>
                                    }
                                    key="杂项"
                                >
                                    杂项
                                </TabPane>
                                <TabPane
                                    tab={
                                        <>
                                            <span>树洞</span>
                                            <Badge count={5} overflowCount={9999} offset={[10, -10]}></Badge>
                                        </>
                                    }
                                    key="树洞"
                                >
                                    树洞
                                </TabPane>
                                <TabPane
                                    tab={
                                        <>
                                            <span>句子收藏</span>
                                            <Badge count={111} overflowCount={9999} offset={[10, -10]}></Badge>
                                        </>
                                    }
                                    key="句子收藏"
                                >
                                    句子收藏
                                </TabPane>
                                <TabPane
                                    tab={
                                        <>
                                            <span>公告</span>
                                            <Badge count={"?"} overflowCount={9999} offset={[10, -10]}></Badge>
                                        </>
                                    }
                                    key="公告"
                                >
                                    <Timeline mode={"left"} reverse={true} pending="To be continued...">
                                        <Timeline.Item label="2015-09-01">Create a services</Timeline.Item>
                                        <Timeline.Item label="2015-09-01 09:12:11">
                                            <p>Solve initial network problems</p>
                                            <p>Solve initial network problems</p>
                                        </Timeline.Item>
                                        <Timeline.Item label="2015-09-01 09:12:13">Technical testing</Timeline.Item>
                                    </Timeline>
                                </TabPane>
                                <TabPane
                                    tab={
                                        <>
                                            <span>搜索结果</span>
                                            <Badge count={null} overflowCount={9999} offset={[10, -10]}></Badge>
                                        </>
                                    }
                                    key="搜索结果"
                                >
                                    搜索结果
                                </TabPane>
                            </Tabs>
                        </Content>
                        <HyggeFooter/>
                    </Layout>
                )}
            </IndexContainerContext.Consumer>
        );
    }
}