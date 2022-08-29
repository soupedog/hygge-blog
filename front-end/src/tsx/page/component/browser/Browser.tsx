import * as React from "react"
import {LogHelper, TimeHelper, UrlHelper} from "../../../utils/UtilContainer";

import {Affix, Breadcrumb, Card, Layout, Space, Tree} from 'antd';
import {HyggeFooter} from "../HyggeFooter";
import {ArticleDto} from "../../../rest/ApiClient";
import Vditor from "vditor";
import $ from "jquery";
import HyggeBrowserHeader from "../HyggeBrowserHeader";
import {MusicPlayerBox} from "./inner/MusicPlayerBox";
import {DashboardTwoTone, DownOutlined, EditTwoTone, EyeOutlined, EyeTwoTone} from "@ant-design/icons";
import {AntdTreeNodeInfo, MdHelper} from "../../../utils/MdHelper";

const {Sider, Content} = Layout;

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
            rootTocTreeList: [{key: "test"}]
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
                        <Card title={_react.props.currentArticle.title} bordered={false}
                              style={{marginTop: '10px'}}>
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
                                              key={"lastUpdate_ts_" + this.props.currentArticle.aid}/>
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
                        <Affix style={{
                            zIndex: 9999,
                            position: 'absolute',
                            top: 164,
                            right: 0,
                            width: "100%"
                        }} offsetTop={164}>
                            {(_react.state.rootTocTreeList.length < 1) ? null :
                                <div style={{paddingLeft: "20px"}}>
                                    <div className="tocTitle">目录</div>
                                    <Tree
                                        showLine={true}
                                        treeData={_react.state.rootTocTreeList}
                                        switcherIcon={<DownOutlined/>}
                                        onSelect={(selectedKeys, info) => {
                                            let anchor = $("#" + info.node.value);
                                            $('html, body').animate({scrollTop: anchor.offset()!.top - 64}, 300);
                                        }}
                                    >
                                    </Tree>
                                </div>
                            }
                        </Affix>
                    </Sider>
                </Layout>
                <HyggeFooter/>
            </Layout>
        );
    }

    componentDidMount() {
        let _react = this;

        if (this.props.currentArticle != null) {
            document.title = this.props.currentArticle.title;

            Vditor.preview(document.getElementById('preview') as HTMLDivElement,
                _react.props.currentArticle.content,
                {
                    // @ts-ignore
                    mediaRenderEnable: false,
                    mode: "dark",
                    markdown: {
                        sanitize: false,
                        toc: true
                    },
                    cdn: UrlHelper.getVditorCdn(),
                    anchor: 0,
                    hljs: {
                        style: "native",
                        lineNumber: true
                    },
                    after: () => {
                        // 清除代码最大高度限制
                        document.querySelectorAll("code").forEach(item => {
                            item.style.maxHeight = "none";
                        });

                        let as: AntdTreeNodeInfo[] = [];
                        let map: Map<number, AntdTreeNodeInfo> = new Map<number, AntdTreeNodeInfo>();
                        document.querySelectorAll("h1,h2,h3,h4,h5").forEach((item, index) => {
                            let antdTreeNode = {
                                index: index,
                                key: "toc_" + index,
                                nodeName: item.tagName,
                                level: null,
                                title: item.textContent as string,
                                value: item.id,
                                parentNodeIndex: null,
                                children: []
                            };

                            as.push(antdTreeNode);
                            map.set(index, antdTreeNode);
                        });

                        let currentTOC = MdHelper.getTocTree({
                            currentTOCArray: as,
                            allTocNodeMap: map,
                            errorCallback: null
                        });

                        console.log(currentTOC);

                        _react.setState({rootTocTreeList: currentTOC});
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