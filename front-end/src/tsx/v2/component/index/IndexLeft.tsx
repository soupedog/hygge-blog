import React from 'react';
import {Menu, MenuProps, message, notification} from "antd";
import Sider from "antd/es/layout/Sider";
import {LinkOutlined, QuestionCircleOutlined} from "@ant-design/icons";
import {IndexContext} from "../../page/Index";
import {class_index_title} from "../properties/ElementNameContainer";

function IndexLeft() {
    const menuOnClick: MenuProps['onClick'] = e => {
        switch (e.key) {
            case "友链":
                message.warning('暂时还没有，有人在期待着一场 PY 交易嘛~', 2);
                break;
            case "关于":
                notification.info({
                    duration: 5,
                    message: '关于',
                    description:
                        '本站前端页面基于 React 、Antd、APlayer、React-Markdown 开发，后端基于 Spring Boot 全家桶开发。目标使用场景为 PC ，对手机端提供少数功能，平板将被视为手机端。本站全部音频、图片素材来源于网络，若侵犯了您的权益，请联系 xavierpe@qq.com 以便及时删除争议素材。',
                });
                break;
        }
    };

    return (
        <IndexContext.Consumer>
            {({menuFolded, updateMenuFolded}) => (
                <Sider trigger={null} collapsible collapsed={menuFolded}>
                    <div className={class_index_title + " autoWrap autoOmit"}>{menuFolded ? "宅" : "我的小宅子"}</div>
                    <Menu theme={"dark"} mode={"inline"} selectable={false}
                          items={items}
                          onClick={menuOnClick}
                    >
                    </Menu>
                </Sider>
            )}
        </IndexContext.Consumer>
    );
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

export default IndexLeft;