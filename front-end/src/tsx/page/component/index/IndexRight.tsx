import * as React from "react"

import {Badge, Layout, Tabs, Timeline} from 'antd';
import {IndexContainerContext} from "../../context/HyggeContext";
import {IndexContainerState} from "../../IndexContainer";
import {LogHelper} from '../../../utils/UtilContainer';
import {HyggeFooter} from "../HyggeFooter";
import HyggeIndexHeader from "../HyggeIndexHeader";

const {Content} = Layout;
const {TabPane} = Tabs;

// 描述该组件 props 数据类型
export interface IndexRightProps {
}

// 描述该组件 states 数据类型
export interface IndexRightState {
}

export class IndexRight extends React.Component<IndexRightProps, IndexRightState> {
    constructor(props: IndexRightProps) {
        super(props);
        this.state = {};
        LogHelper.info({className: "IndexRight", msg: "初始化成功"});
    }

    render() {
        return (
            <IndexContainerContext.Consumer>
                {(state: IndexContainerState) => (
                    <Layout className="right_box site-layout">
                        <HyggeIndexHeader key={"he"}/>
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