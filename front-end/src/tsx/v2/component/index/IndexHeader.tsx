import React, {createContext, useMemo, useState} from 'react';
import {Header} from "antd/es/layout/layout";
import {Col, Row, Switch, Tooltip} from "antd";
import clsx from "clsx";
import {MenuFoldOutlined, MenuUnfoldOutlined} from "@ant-design/icons";
import { IndexContext } from '../../page/Index';

function IndexHeader() {
    return (
        <IndexContext.Consumer>
            {({menuFolded, updateMenuFolded}) => (
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
                        "headMenuSmallMode": !menuFolded,
                        "headMenuBigMode": menuFolded
                    })}>
                        <Col md={2} xl={12}>
                            <Tooltip placement="bottom" title={menuFolded ? "展开" : "收起"}>
                                {React.createElement(menuFolded ? MenuUnfoldOutlined : MenuFoldOutlined, {
                                    className: 'trigger',
                                    onClick: () => updateMenuFolded(!menuFolded),
                                })}
                            </Tooltip>
                        </Col>
                        <Col md={22} xl={12}>
                            <Row gutter={[0, 0]} justify="end">
                                <Col md={1} xl={4}>{/*占位符*/}</Col>
                                <Col id={"searchModeSwitch"} md={3} xl={3}>
                                    <Tooltip placement="bottom" title={"搜索类型"}>
                                        <Switch checkedChildren="文章" unCheckedChildren="句子" defaultChecked/>
                                    </Tooltip>
                                </Col>
                                <Col md={8} xl={11}>
                                    {/*<Search style={{marginTop: 16}} placeholder="搜索关键字"*/}
                                    {/*        allowClear*/}
                                    {/*        enterButton*/}
                                    {/*        size="middle"*/}
                                    {/*        onSearch={(value) => {*/}
                                    {/*            console.log(value)*/}
                                    {/*            console.log(!PropertiesHelper.isStringNotEmpty(value))*/}
                                    {/*            if (!PropertiesHelper.isStringNotEmpty(value)) {*/}
                                    {/*                message.warn("查询关键字不可为空", 3);*/}
                                    {/*                return;*/}
                                    {/*            }*/}

                                    {/*            let searchType: SearchType;*/}
                                    {/*            if (document.querySelector("#searchModeSwitch")!.querySelector("button")!.ariaChecked == "true") {*/}
                                    {/*                searchType = SearchType.ARTICLE;*/}
                                    {/*            } else {*/}
                                    {/*                searchType = SearchType.QUOTE;*/}
                                    {/*            }*/}
                                    {/*            state.fetchFuzzySearchViewInfo!(1, 5, value, searchType);*/}
                                    {/*        }}*/}
                                    {/*/>*/}
                                </Col>
                                <Col md={1} xl={1}>{/*占位符*/}</Col>
                                <Col md={3} xl={3}>
                                    {/*{state.currentUser != null ? <HyggeUserMenu currentUser={state.currentUser}/> :*/}
                                    {/*    <Button type="primary"*/}
                                    {/*            onClick={() => {*/}
                                    {/*                this.props.router.navigate("/signin");*/}
                                    {/*            }}*/}
                                    {/*    >登录</Button>}*/}
                                </Col>
                                <Col md={2} xl={2} className={"textCenter"}>
                                    {/*<Spin spinning={state.netWorkArrayCounter!.length > 0}/>*/}
                                </Col>
                            </Row>
                        </Col>
                    </Row>
                </Header>
            )}
        </IndexContext.Consumer>
    );
}

export default IndexHeader;