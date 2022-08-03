import * as React from "react"
import {ErrorInfo} from "react"
import {LogHelper} from '../utils/LogHelper';

import '../../css/default.css';
import '../../css/index.less';
import '../../css/index.scss';
import 'antd/dist/antd.min.css';

import {
    Avatar,
    Badge,
    Card,
    Collapse,
    ConfigProvider,
    Divider,
    Input,
    Layout,
    Menu,
    message,
    notification,
    Space,
    Spin,
    Switch,
    Tabs
} from 'antd';
import {
    GithubOutlined,
    LinkOutlined,
    MenuFoldOutlined,
    MenuUnfoldOutlined,
    QuestionCircleOutlined
} from '@ant-design/icons';

import clsx from "clsx";
import zhCN from "antd/lib/locale/zh_CN";

const {Header, Sider, Content} = Layout;
const {Panel} = Collapse;
const {TabPane} = Tabs;
const {Search} = Input;


// 描述该组件 props 数据类型
export interface IndexContainerProps {
    boardResumeArray?: Array<any>;
    sentenceCollectionTotalCount?: number;
    framework?: string;
}

// 描述该组件 states 数据类型
export interface IndexContainerStatus {
    hasError?: boolean;
    menuSmallMode?: boolean;
    requestList?: Array<boolean>;
    sentenceCollectionTotalCount?: number | null;
    searchResult?: Array<any>;
}

export class AntdDemo extends React.Component<IndexContainerProps, IndexContainerStatus> {
    constructor(props: any) {
        super(props);
        this.state = {
            hasError: false,
            menuSmallMode: true,
            requestList: [],
            sentenceCollectionTotalCount: this.props.sentenceCollectionTotalCount,
            searchResult: []
        };
        LogHelper.info("AntdDemo", "constructor", "----------", false);
    }

    static getDerivedStateFromProps(nextProps?: IndexContainerProps, nextState?: IndexContainerStatus) {
        LogHelper.info("AntdDemo", "getDerivedStateFromProps", "----------", false);
        LogHelper.debug("AntdDemo", "getDerivedStateFromProps-nextProps", nextProps, true);
        LogHelper.debug("AntdDemo", "getDerivedStateFromProps-nextState", nextState, true);
        return nextProps;
    }

    render() {
        if (this.state.hasError) {
            // 你可以渲染任何自定义的降级 UI
            return <h1>Something went wrong.</h1>;
        }
        let _react = this;
        return (
            <ConfigProvider locale={zhCN}>
                <Sider style={{position: "fixed", zIndex: 100}} trigger={null} collapsible
                       collapsed={_react.state.menuSmallMode}>
                    <div
                        className="website-title autoWrap autoOmit">{_react.state.menuSmallMode ? "宅" : "我的小宅子"}</div>
                    <Menu theme="dark" mode="inline" selectable={false}>
                        <Menu.Item key="MenuItem_1">
                            <i className="anticon anticon-link">
                                <Avatar size={14}
                                        src="https://www.xavierwang.cn/static/我的头像.png"/>
                            </i>
                            <span>Xavier</span>
                        </Menu.Item>
                        <Menu.Item key="MenuItem_2" onClick={() => {
                        }}>
                            <i className="anticon anticon-link">
                                <Avatar size={14}
                                        src={"https://www.xavierwang.cn/static/我的头像.png"}/>
                            </i>
                            <span>CSDN(已停更)</span>
                        </Menu.Item>
                        <Menu.Item key="MenuItem_3" onClick={() => {
                        }}>
                            <GithubOutlined/>
                            <span>GitHub</span>
                        </Menu.Item>
                        <Menu.Item key="MenuItem_4" onClick={() => {
                            message.warn('暂时还没有，有人在期待着一场 PY 交易嘛~', 2);
                        }}>
                            <LinkOutlined/>
                            <span>友链</span>
                        </Menu.Item>
                        <Menu.Item key="MenuItem_5" onClick={() => {
                            notification.info({
                                message: '关于',
                                description:
                                    '本站前端基于 React 、Antd、Vditor、APlayer 开发，后端基于 Spring Boot 全家桶开发，已在我的 Github 个人仓库开源。目标使用场景为 PC ，对手机端提供少数功能，平板将被视为手机端。本站全部音频、图片素材来源于网络，若侵犯了您的权益，请联系 xavierpe@qq.com 以便及时删除侵权素材。',
                            });
                        }}>
                            <QuestionCircleOutlined/>
                            <span>关于</span>
                        </Menu.Item>
                    </Menu>
                </Sider>
                <Layout>
                    <Sider trigger={null} collapsible collapsed={_react.state.menuSmallMode} key={"MenuOccupySeat"}>
                    </Sider>
                    <Layout>
                        <Header id="indexHeader" style={{
                            position: "fixed",
                            zIndex: 1,
                            width: "100%",
                            padding: 0,
                            color: "#fff"
                        }}>
                            {React.createElement(_react.state.menuSmallMode ? MenuUnfoldOutlined : MenuFoldOutlined, {
                                className: 'indexMenuSwitch',
                                onClick: () => {
                                    _react.setState({"menuSmallMode": !_react.state.menuSmallMode});
                                },
                            })}
                            <div className={clsx({
                                "floatRight": true,
                                "headMenuSmallMode": !_react.state.menuSmallMode,
                                "headMenuBigMode": _react.state.menuSmallMode
                            })}>
                                <div className={"floatRight"} style={{marginLeft: "40px"}}>
                                    <Space size="middle">
                                        <Spin spinning={_react.state.requestList?.length! > 0} size="large"/>
                                    </Space>
                                </div>
                                <div className={"floatRight"}>
                                </div>
                                <div className={"floatRight"} style={{padding: "17px"}}>
                                    <Search placeholder="搜索关键字"
                                            allowClear
                                            enterButton
                                            size="middle"
                                            onSearch={(value) => {
                                            }}
                                    />
                                </div>
                                <div id={"searchModeSwitch"} className={"floatRight"}>
                                    <Switch checkedChildren="搜索模式：文章" unCheckedChildren="搜索模式：收藏"
                                            defaultChecked/>
                                </div>
                            </div>
                        </Header>
                        <Content
                            className="site-layout-background myContent"
                            style={{
                                margin: '88px 16px',
                                padding: 24,
                                minHeight: 800,
                            }}
                        ></Content>
                    </Layout>
                </Layout>
            </ConfigProvider>
        );
    }

    shouldComponentUpdate(nextProps?: IndexContainerProps, nextState?: IndexContainerStatus, nextContext?: any) {
        LogHelper.info("AntdDemo", "shouldComponentUpdate", "----------", false);
        LogHelper.debug("AntdDemo", "shouldComponentUpdate-nextProps", nextProps, true);
        LogHelper.debug("AntdDemo", "shouldComponentUpdate-nextState", nextState, true);
        LogHelper.debug("AntdDemo", "shouldComponentUpdate-nextContext", nextContext, true);
        return true;
    }

    componentDidMount() {
        document.querySelector("code");

        LogHelper.info("AntdDemo", "componentDidMount", "----------", false);
    }

    getSnapshotBeforeUpdate(prevProps?: IndexContainerProps, prevState?: IndexContainerStatus) {
        LogHelper.info("AntdDemo", "getSnapshotBeforeUpdate", "----------", false);
        LogHelper.debug("AntdDemo", "getSnapshotBeforeUpdate-prevProps", prevProps, true);
        LogHelper.debug("AntdDemo", "getSnapshotBeforeUpdate-prevState", prevState, true);
        return null;
    }

    componentDidUpdate(prevProps?: IndexContainerProps, prevState?: IndexContainerStatus, snapshot?: any) {
        LogHelper.info("AntdDemo", "componentDidUpdate", "----------", false);
        LogHelper.debug("AntdDemo", "componentDidUpdate-prevProps", prevProps, true);
        LogHelper.debug("AntdDemo", "componentDidUpdate-prevState", prevState, true);
        LogHelper.debug("AntdDemo", "componentDidUpdate-snapshot", snapshot, true);
    }

    static getDerivedStateFromError(error?: Error) {
        // 更新 state 使下一次渲染可以显示降级 UI
        return {hasError: true};
    }

    componentDidCatch(error?: Error, info?: ErrorInfo) {
        LogHelper.info("AntdDemo", "componentDidCatch", "----------", false);
        LogHelper.debug("AntdDemo", "componentDidCatch-error", error, true);
        LogHelper.debug("AntdDemo", "componentDidCatch-info", info, true);
    }
}