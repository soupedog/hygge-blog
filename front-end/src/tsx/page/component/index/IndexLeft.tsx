import * as React from "react"
import {LogHelper} from '../../../utils/UtilContainer';
import {Layout, Menu, MenuProps, message, notification} from 'antd';
import {LinkOutlined, QuestionCircleOutlined} from '@ant-design/icons';
import {IndexContainerContext} from "../../context/HyggeContext";
import {IndexContainerState} from "../../IndexContainer";

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
                {(state: IndexContainerState) => (
                    <Sider trigger={null} className={"left_box"} collapsible collapsed={state.folded}>
                        <div className={"page-title autoWrap autoOmit"}>{state.folded ? "宅" : "我的小宅子"}</div>
                        <Menu theme={"dark"} mode={"inline"} selectable={false}
                              items={items}
                              onClick={this.menuOnClick}>
                        </Menu>
                    </Sider>
                )}
            </IndexContainerContext.Consumer>
        );
    }

    menuOnClick: MenuProps['onClick'] = e => {
        switch (e.key) {
            case "友链":
                message.warn('暂时还没有，有人在期待着一场 PY 交易嘛~', 2);
                break;
            case "关于":
                notification.info({
                    message: '关于',
                    description:
                        '本站前端基于 React 、Antd、Vditor、APlayer 开发，后端基于 Spring Boot 全家桶开发，已在我的 Github 个人仓库开源。目标使用场景为 PC ，对手机端提供少数功能，平板将被视为手机端。本站全部音频、图片素材来源于网络，若侵犯了您的权益，请联系 xavierpe@qq.com 以便及时删除争议素材。',
                });
                break;
        }
    };
}

type MenuItem = Required<MenuProps>['items'][number];

function getMenuItem(label: React.ReactNode,
                     key?: React.Key | null,
                     icon?: React.ReactNode,
                     children?: MenuItem[],
                     theme?: 'light' | 'dark'): MenuItem {
    return {
        key,
        icon,
        children,
        label,
        theme,
    } as MenuItem;
}

const items: MenuItem[] = [
    getMenuItem('友链', '友链', <LinkOutlined/>),
    getMenuItem('关于', '关于', <QuestionCircleOutlined/>),
];
