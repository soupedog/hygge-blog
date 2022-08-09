import * as React from "react"

import {Badge, Collapse, Layout, Tabs, Timeline} from 'antd';
import {IndexContainerContext} from "../../context/HyggeContext";
import {IndexContainerState} from "../../IndexContainer";
import {LogHelper, TimeHelper} from '../../../utils/UtilContainer';
import {HyggeFooter} from "../HyggeFooter";
import HyggeIndexHeader from "../HyggeIndexHeader";
import {ArticleOverviewContainer} from "./inner/ArticleOverviewContainer";
import {QuoteContainer} from "./inner/QuoteContainer";
import {CategoryContainer} from "./inner/CategoryContainer";
import {SearchResultContainer} from "./inner/SearchResultContainer";

const {Panel} = Collapse;
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
        let _react = this;
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
                                margin: '24px 60px',
                                marginTop: 100,
                                padding: 24,
                                minHeight: window.innerHeight - 282,
                            }}
                        >
                            <CategoryContainer/>
                            <br/>
                            <Tabs defaultActiveKey="编程" type="card" size={"large"}
                                  onChange={(key) => {
                                      switch (key) {
                                          case "句子收藏":
                                          case "公告":
                                              state.updateRootStatus!({categoryFolded: true});
                                              break;
                                          case "搜索结果":
                                              break;
                                          default :
                                              state.updateRootStatus!({
                                                  categoryFolded: false,
                                                  currentTid: key.substring(8)
                                              });
                                      }
                                  }}>
                                {
                                    state.topicOverviewInfoList?.map((item) => {
                                        return (
                                            <TabPane key={"tab_pane" + item.topicInfo.tid}
                                                     tab={
                                                         <>
                                                             <span>{item.topicInfo.topicName}</span>
                                                             <Badge count={item.totalCount} overflowCount={9999}
                                                                    offset={[10, -10]}></Badge>
                                                         </>
                                                     }>
                                                <ArticleOverviewContainer isMaintainer={state.currentUser != null}
                                                                          tid={item.topicInfo.tid}/>
                                            </TabPane>
                                        )
                                    })
                                }
                                <TabPane
                                    tab={
                                        <>
                                            <span>句子收藏</span>
                                            <Badge count={state.quoteResponse?.totalCount} overflowCount={9999}
                                                   offset={[10, -10]}></Badge>
                                        </>
                                    }
                                    key="句子收藏"
                                >
                                    <QuoteContainer isMaintainer={state.currentUser != null}/>
                                </TabPane>
                                <TabPane
                                    tab={
                                        <>
                                            <span id={"searchTap"}>搜索结果</span>
                                            <Badge count={null} overflowCount={9999} offset={[10, -10]}></Badge>
                                        </>
                                    }
                                    key="搜索结果"
                                >
                                    <SearchResultContainer isMaintainer={state.currentUser != null}/>
                                </TabPane>
                                <TabPane
                                    tab={
                                        <>
                                            <span>公告</span>
                                            <Badge count={state.announcementDtoList?.length} overflowCount={9999}
                                                   offset={[10, -10]}></Badge>
                                        </>
                                    }
                                    key="公告"
                                >
                                    <Timeline mode={"left"} reverse={true} pending="To be continued...">
                                        {
                                            state.announcementDtoList?.map(((item, index) => {
                                                return (
                                                    <Timeline.Item
                                                        key={"announcement_" + item.announcementId + "_" + index}
                                                        color={item.color}
                                                        label={TimeHelper.formatTimeStampToString(item.createTs)}>
                                                        {
                                                            item.paragraphList.map((p, index) => {
                                                                return (
                                                                    <p key={"p_" + item.announcementId + "_" + index}>{p}</p>
                                                                )
                                                            })
                                                        }
                                                    </Timeline.Item>
                                                )
                                            }))
                                        }
                                    </Timeline>
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