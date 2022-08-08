import * as React from "react"
import {LogHelper} from '../../utils/UtilContainer';
import {IndexContainerState, SearchType} from "../IndexContainer";
import {IndexContainerContext} from "../context/HyggeContext";
import {Button, Col, Input, Layout, Row, Spin, Switch, Tooltip} from "antd";
import clsx from "clsx";
import {MenuFoldOutlined, MenuUnfoldOutlined} from "@ant-design/icons";
import HyggeUserMenu from "./HyggeUserMenu";
import {ReactRouter, withRouter} from "../../utils/ReactRouterHelper";

const {Search} = Input;
const {Header} = Layout;

// 描述该组件 props 数据类型
export interface HyggeIndexHeaderProps {
    router: ReactRouter;
}

// 描述该组件 states 数据类型
export interface HyggeIndexHeaderState {
}

class HyggeIndexHeader extends React.Component<HyggeIndexHeaderProps, HyggeIndexHeaderState> {
    constructor(props: HyggeIndexHeaderProps) {
        super(props);
        this.state = {};
        LogHelper.info({className: "HyggeIndexHeader", msg: "初始化成功"});
    }

    render() {
        return (
            <IndexContainerContext.Consumer>
                {(state: IndexContainerState) => (
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
                            "headMenuSmallMode": !state.menuFolded,
                            "headMenuBigMode": state.menuFolded
                        })}>
                            <Col md={2} xl={12}>
                                <Tooltip placement="bottom" title={state.menuFolded ? "展开" : "收起"}>
                                    {React.createElement(state.menuFolded ? MenuUnfoldOutlined : MenuFoldOutlined, {
                                        className: 'trigger',
                                        onClick: () => state.updateRootStatus!({menuFolded: !state.menuFolded}),
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
                                        {state.currentUser != null ? <HyggeUserMenu/> :
                                            <Button type="primary"
                                                    onClick={() => {
                                                        this.props.router.navigate("/signin");
                                                    }}
                                            >登录</Button>}

                                    </Col>
                                    <Col md={2} xl={2} className={"textCenter"}>
                                        <Spin spinning/>
                                    </Col>
                                </Row>
                            </Col>
                        </Row>
                    </Header>
                )}
            </IndexContainerContext.Consumer>
        );
    }
}

export default withRouter(HyggeIndexHeader)
