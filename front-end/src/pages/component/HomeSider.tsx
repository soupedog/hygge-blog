import * as React from 'react';
import {useContext} from 'react';
import {HomeContext} from '../context/HomeContext.tsx';
import {Layout, Menu, type MenuProps, message, notification} from 'antd';
import clsx from 'clsx';
import {LinkOutlined, QuestionCircleOutlined} from '@ant-design/icons';

const {Sider} = Layout;
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

const homeSiderTitleStyle: React.CSSProperties = {
    height: '32px',
    margin: '16px',
    background: 'rgba(255, 255, 255, 0.3)',
    fontSize: '18px',
    textAlign: 'center',
    fontWeight: 'bold',
    lineHeight: '32px',
    color: 'white'
};

const menuOnClick: MenuProps['onClick'] = e => {
    switch (e.key) {
        case '友链':
            message.warning({content: '暂时还没有，有人在期待着一场 PY 交易嘛~', duration: 2});
            break;
        case '关于':
            notification.info({
                placement: 'bottomRight',
                duration: 5,
                title: '关于',
                description:
                    '本站前端页面基于 React 、Antd、APlayer、Md-Editor-Rt 开发，后端基于 Spring Boot 全家桶开发。目标使用场景为 PC ，对手机端提供少数功能，平板将被视为手机端。本站全部音频、图片素材来源于网络，若侵犯了您的权益，请联系 xavierpe@qq.com 以便及时删除争议素材。',
            });
            break;
    }
};

export default function HomeSider() {
    const {collapsed} = useContext(HomeContext);

    return (
        <Sider trigger={null} collapsible collapsed={collapsed}>
            <div className={clsx(['auto-wrap', 'auto-omit'])} style={homeSiderTitleStyle}>{collapsed ? '宅' : '我的小宅子'}</div>
            <Menu theme={'dark'} mode={'inline'} selectable={false}
                  items={items}
                  onClick={menuOnClick}
            />
        </Sider>
    );
}
