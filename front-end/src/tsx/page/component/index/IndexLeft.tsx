import * as React from "react"
import {LogHelper, UrlHelper} from '../../../utils/UtilContainer';
import {Layout, Menu, message, notification} from 'antd';
import {GithubOutlined, LinkOutlined, QuestionCircleOutlined} from '@ant-design/icons';
import {IndexContainerContext} from "../../context/HyggeContext";
import {IndexContainerStatus} from "../../IndexContainer";

const {Sider} = Layout;

// 描述该组件 props 数据类型
export interface IndexLeftProps {
}

// 描述该组件 states 数据类型
export interface IndexLeftStatus {
}

export class IndexLeft extends React.Component<IndexLeftProps, IndexLeftStatus> {
    constructor(props: IndexLeftProps) {
        super(props);
        this.state = {};
        LogHelper.info({className: "IndexLeft", msg: "初始化成功"});
    }

    render() {
        return (
            <IndexContainerContext.Consumer>
                {(status: IndexContainerStatus) => (
                    <>
                        <Sider className={"left_box"} collapsible collapsed={status.folded}>
                            <div className={"page-title autoWrap autoOmit"}>{status.folded ? "宅" : "我的小宅子"}</div>
                            <Menu theme={"dark"} mode={"inline"} selectable={false}>
                                <Menu.Item key={"menu_item_csdn"} onClick={() => {
                                    UrlHelper.openNewPage({
                                        finalUrl: "https://blog.csdn.net/u014430366",
                                        inNewTab: true
                                    });
                                }}>
                                    <i className={"anticon anticon-link"}>
                                        <i className={"iconfont icon-csdn"}></i>
                                    </i>
                                    <span>CSDN(已停更)</span>
                                </Menu.Item>
                                <Menu.Item key={"menu_item_github"} onClick={() => {
                                    UrlHelper.openNewPage({
                                        finalUrl: "https://github.com/SoupeDog",
                                        inNewTab: true
                                    });
                                }}>
                                    <GithubOutlined/>
                                    <span>GitHub</span>
                                </Menu.Item>
                                <Menu.Item key={"menu_item_friend_link"} onClick={() => {
                                    message.warn('暂时还没有，有人在期待着一场 PY 交易嘛~', 2);
                                }}>
                                    <LinkOutlined/>
                                    <span>友链</span>
                                </Menu.Item>
                                <Menu.Item key={"menu_item_about"} onClick={() => {
                                    notification.info({
                                        message: '关于',
                                        description:
                                            '本站前端基于 React 、Antd、Vditor、APlayer 开发，后端基于 Spring Boot 全家桶开发，已在我的 Github 个人仓库开源。目标使用场景为 PC ，对手机端提供少数功能，平板将被视为手机端。本站全部音频、图片素材来源于网络，若侵犯了您的权益，请联系 xavierpe@qq.com 以便及时删除争议素材。',
                                    });
                                }}>
                                    <QuestionCircleOutlined/>
                                    <span>关于</span>
                                </Menu.Item>
                            </Menu>
                        </Sider>
                    </>
                )}
            </IndexContainerContext.Consumer>
        );
    }
}
