import * as React from "react"
import {LogHelper, UrlHelper, WindowsEventHelper} from "../../../utils/UtilContainer";

import {Card, Layout, Tooltip} from 'antd';
import {RollbackOutlined} from "@ant-design/icons";
import clsx from "clsx";
import {HyggeFooter} from "../HyggeFooter";
import {ArticleDto} from "../../../rest/ApiClient";
import Vditor from "vditor";

const {Header, Sider, Content} = Layout;

// 描述该组件 props 数据类型
export interface BrowserProps {
    currentArticle: ArticleDto
}

// 描述该组件 states 数据类型
export interface BrowserStatus {
    headerTransparent: boolean,
    rootTocTreeList: any[]
}

export class Browser extends React.Component<BrowserProps, BrowserStatus> {
    constructor(props: BrowserProps) {
        super(props);
        this.state = {
            headerTransparent: true,
            rootTocTreeList: []
        };
        LogHelper.info({className: "Browser", msg: "初始化成功"});
    }

    render() {
        let _react = this;
        return (
            <Layout>
                <Header style={{position: 'fixed', zIndex: 1, width: '100%'}}
                        className={clsx({
                            "backgroundTransparent": this.state.headerTransparent
                        })}>
                    <div className={"floatLeft"}>
                        <Tooltip placement="bottom" title={"返回首页"}>
                            <RollbackOutlined onClick={() => {
                                UrlHelper.openNewPage({finalUrl: UrlHelper.getBaseUrl(), inNewTab: false});
                            }} style={{color: "#fff", fontWeight: "bold", fontSize: "24px", lineHeight: "64px"}}/>
                        </Tooltip>
                    </div>
                    <div className={"floatRight"}>
                        {/*<HyggeIndexHeader key={"browser_header"}/>*/}
                    </div>
                </Header>
                <div id="mainImage" style={{
                    width: "100%",
                    height: "400px",
                    backgroundSize: "cover",
                    background: "url(" + _react.props.currentArticle.imageSrc + ") no-repeat center"
                }}/>
                {/*<MusicPlayerBox configuration={_react.props.currentArticle.configuration}/>*/}
                <Layout>
                    <Content id="mainView">
                        <Card title={_react.props.currentArticle.title} bordered={false}
                              style={{marginTop: '10px'}}>
                            {/*<ArticleCategoryBreadcrumb board={_react.props.article.board}*/}
                            {/*                           articleCategoryList={_react.props.article.articleCategoryList}/>*/}
                            <div style={{marginTop: "20px"}}>
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

        WindowsEventHelper.addCallback_Scroll({
            name: "APPBar 透明判定", delta: 50, callbackFunction: function ({currentScrollY}) {
                if (currentScrollY > 336) {
                    _react.setState({headerTransparent: false});
                } else {
                    _react.setState({headerTransparent: true});
                }
            }
        });
        WindowsEventHelper.start_OnScroll();

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
