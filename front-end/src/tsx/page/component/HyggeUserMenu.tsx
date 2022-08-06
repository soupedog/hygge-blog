import * as React from "react"
import {LogHelper, UrlHelper} from '../../utils/UtilContainer';
import {IndexContainerState} from "../IndexContainer";
import {IndexContainerContext} from "../context/HyggeContext";
import {Avatar, Dropdown, Menu, MenuProps, message} from "antd";
import {CloseCircleOutlined, EditOutlined} from '@ant-design/icons';
import {UserService} from "../../rest/ApiClient";
import {ReactRouter, withRouter} from "../../utils/ReactRouterHelper";

// 描述该组件 props 数据类型
export interface HyggeUserMenuProps {
    router: ReactRouter;
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
        return (
            <IndexContainerContext.Consumer>
                {(state: IndexContainerState) => (
                    <Dropdown overlay={
                        <Menu items={items} onClick={(event) => {
                            let aid = UrlHelper.getQueryString("aid");
                            let finalUrl = UrlHelper.getBaseUrl();
                            switch (event.key) {
                                case "signOut":
                                    UserService.removeCurrentUser();
                                    message.success("登出成功，1 s 后将跳转回首页。");
                                    let currentSecretKey = UrlHelper.getQueryString("secretKey");
                                    if (currentSecretKey != null) {
                                        finalUrl = finalUrl + "?secretKey=" + currentSecretKey;
                                    } else {
                                        this.props.router.navigate("")
                                    }
                                    UrlHelper.openNewPage({finalUrl: finalUrl, inNewTab: false})
                                    break;
                                case "editArticle":
                                    if (aid != null) {
                                        this.props.router.navigate("editor/article/" + aid, {replace: false})
                                    } else {
                                        this.props.router.navigate("editor/article/")
                                    }
                                    break;
                                case "editQuote":
                                    this.props.router.navigate("editor/quote")
                                    break;
                            }
                        }}/>
                    }>
                        <Avatar className={"pointer"} size={48} src={state.currentUser?.userAvatar}/>
                    </Dropdown>
                )}
            </IndexContainerContext.Consumer>
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