import * as React from "react"
import {LogHelper, UrlHelper} from '../../utils/UtilContainer';
import {Avatar, Dropdown, Menu, MenuProps, message} from "antd";
import {CloseCircleOutlined, EditOutlined} from '@ant-design/icons';
import {UserDto, UserService} from "../../rest/ApiClient";
import {ReactRouter, withRouter} from "../../utils/ReactRouterHelper";

// 描述该组件 props 数据类型
export interface HyggeUserMenuProps {
    router: ReactRouter,
    currentUser?: UserDto | null,
}

// 描述该组件 states 数据类型
export interface HyggeUserMenuState {

}

export class HyggeUserMenu extends React.Component<HyggeUserMenuProps, HyggeUserMenuState> {
    constructor(props: HyggeUserMenuProps) {
        super(props);
        this.state = {};
        LogHelper.info({className: "HyggeIndexHeader", msg: "初始化成功"});
    }

    render() {
        if (this.props.currentUser == null) {
            return null;
        }

        return (
            <Dropdown overlay={
                <Menu items={items} onClick={(event) => {
                    let aid = this.props.router.params.aid;
                    let finalUrl = UrlHelper.getBaseUrl();
                    switch (event.key) {
                        case "signOut":
                            UserService.removeCurrentUser();
                            message.success("登出成功，1 s 后将跳转回首页。");
                            let currentSecretKey = UrlHelper.getQueryString("secretKey");
                            if (currentSecretKey != null) {
                                finalUrl = finalUrl + "?secretKey=" + currentSecretKey;
                            } else {
                                UrlHelper.openNewPage({inNewTab: false})
                            }
                            UrlHelper.openNewPage({finalUrl: finalUrl, inNewTab: false})
                            break;
                        case "editArticle":
                            if (aid != null) {
                                UrlHelper.openNewPage({path: "#/editor/article/" + aid, inNewTab: false})
                            } else {
                                UrlHelper.openNewPage({path: "#/editor/article", inNewTab: false})
                            }
                            break;
                        case "editQuote":
                            UrlHelper.openNewPage({path: "#/editor/quote", inNewTab: false})
                            break;
                    }
                }}/>
            }>
                <Avatar className={"pointer"} size={48} src={this.props.currentUser!.userAvatar}/>
            </Dropdown>
        );
    }
}

export default withRouter(HyggeUserMenu)


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
    getMenuItem('编辑文章', 'editArticle', <EditOutlined/>),
    getMenuItem('编辑句子收藏', 'editQuote', <EditOutlined/>),
    getMenuItem('登出', 'signOut', <CloseCircleOutlined/>),
];