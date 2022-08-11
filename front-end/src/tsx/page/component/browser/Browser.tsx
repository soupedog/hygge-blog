import * as React from "react"
import {LogHelper, TimeHelper} from "../../../utils/UtilContainer";

import {Breadcrumb, Card, Layout, Space} from 'antd';
import {HyggeFooter} from "../HyggeFooter";
import {ArticleDto} from "../../../rest/ApiClient";
import Vditor from "vditor";
import HyggeBrowserHeader from "../HyggeBrowserHeader";
import {MusicPlayerBox} from "./inner/MusicPlayerBox";
import {DashboardTwoTone, EditTwoTone, EyeOutlined, EyeTwoTone} from "@ant-design/icons";

const {Header, Sider, Content} = Layout;

// 描述该组件 props 数据类型
export interface BrowserProps {
    isMaintainer: boolean,
    currentArticle: ArticleDto
}

// 描述该组件 states 数据类型
export interface BrowserStatus {
    rootTocTreeList: any[]
}

export class Browser extends React.Component<BrowserProps, BrowserStatus> {
    constructor(props: BrowserProps) {
        super(props);
        this.state = {
            rootTocTreeList: []
        };
        LogHelper.info({className: "Browser", msg: "初始化成功"});
    }

    render() {
        let _react = this;
        return (
            <Layout>
                <HyggeBrowserHeader/>
                <div id="mainImage" style={{
                    width: "100%",
                    height: "400px",
                    backgroundSize: "cover",
                    background: "url(" + _react.props.currentArticle.imageSrc + ") no-repeat center"
                }}/>
                <MusicPlayerBox configuration={_react.props.currentArticle.configuration}/>
                <Layout>
                    <Content id="mainView">
                        <Card title={_react.props.currentArticle.title} bordered={false}>
                            <Breadcrumb>
                                <Breadcrumb.Item>{this.props.currentArticle.categoryTreeInfo.topicInfo.topicName}</Breadcrumb.Item>
                                {
                                    this.props.currentArticle.categoryTreeInfo.categoryList?.map((articleCategoryInfo, index) => {
                                        return (
                                            <Breadcrumb.Item key={index}>
                                                <span>{articleCategoryInfo.categoryName}</span>
                                            </Breadcrumb.Item>
                                        )
                                    })
                                }
                            </Breadcrumb>
                            <div style={{
                                marginTop: "10px",
                                fontSize: "12px",
                                lineHeight: "20px",
                                color: "#6a737d"
                            }}>
                                <Space size={"middle"}>
                                    <IconText icon={EditTwoTone} text={"字数 " + this.props.currentArticle.wordCount}
                                              key={"word_count_" + this.props.currentArticle.aid}/>
                                    <IconText icon={DashboardTwoTone}
                                              text={"创建于 " + TimeHelper.formatTimeStampToString(this.props.currentArticle.createTs)}
                                              key={"create_ts_" + this.props.currentArticle.aid}/>
                                    <IconText icon={DashboardTwoTone}
                                              text={"最后修改于 " + TimeHelper.formatTimeStampToString(this.props.currentArticle.lastUpdateTs)}
                                              key={"create_ts_" + this.props.currentArticle.aid}/>
                                    <IconText icon={EyeTwoTone} text={"浏览量 " + this.props.currentArticle.pageViews}
                                              key={"page_view_" + this.props.currentArticle.aid}/>
                                    {this.props.isMaintainer ? <IconText icon={EyeOutlined}
                                                                           text={"自浏览 " + this.props.currentArticle.selfPageViews}
                                                                           key={"self_view_" + this.props.currentArticle.aid}/> : null}
                                </Space>
                            </div>
                        </Card>
                        <Card style={{marginTop: "20px"}} bordered={false}>
                            <div id={"preview"}/>
                        </Card>
                    </Content>
                    <Sider trigger={null}
                           collapsed={_react.state.rootTocTreeList.length < 1}
                           width={"20%"} collapsedWidth={0}
                           style={{backgroundColor: "#F0F2F5"}}>
                        {/*<Affix style={{*/}
                        {/*    zIndex: 9999,*/}
                        {/*    position: 'absolute',*/}
                        {/*    top: 164,*/}
                        {/*    right: 0,*/}
                        {/*    width: "100%"*/}
                        {/*}} offsetTop={164}>*/}
                        {/*    {(_react.state.rootTocTreeList.length < 1) ? null :*/}
                        {/*        <div>*/}
                        {/*            <div className="tocTitle">目录</div>*/}
                        {/*            <Tree*/}
                        {/*                showLine={true}*/}
                        {/*                treeData={_react.state.rootTocTreeList}*/}
                        {/*                switcherIcon={<DownOutlined/>}*/}
                        {/*                onSelect={(selectedKeys, info) => {*/}
                        {/*                    @ts-ignore*/}
                        {/*                    $("#preview").find(info.node.nodeName).each(function () {*/}
                        {/*                        let currentTarget = $(this);*/}
                        {/*                        let title = currentTarget.text();*/}
                        {/*                        if (title == info.node.title) {*/}
                        {/*                            // @ts-ignore*/}
                        {/*                            $('html, body').animate({scrollTop: currentTarget.offset().top - 64}, 300);*/}
                        {/*                        }*/}
                        {/*                    });*/}
                        {/*                }}*/}
                        {/*            >*/}
                        {/*            </Tree>*/}
                        {/*        </div>*/}
                        {/*    }*/}
                        {/*</Affix>*/}
                    </Sider>
                </Layout>
                <HyggeFooter/>
            </Layout>
        );
    }

    componentDidMount() {
        let _react = this;

        if (this.props.currentArticle != null) {
            Vditor.preview(document.getElementById('preview') as HTMLDivElement,
                _react.props.currentArticle.content,
                {
                    mode: "dark",
                    markdown: {
                        sanitize: false,
                        toc: true
                    },
                    // cdn:"https://www.xavierwang.cn/static/npm/vditor@3.8.5",
                    anchor: 0,
                    hljs: {
                        style: "native",
                        lineNumber: true
                    },
                    after: () => {
                    }
                });
        }
    }
}

const IconText = ({icon, text}: { icon: React.FC; text: string }) => (
    <Space>
        {React.createElement(icon)}
        {text}
    </Space>
);