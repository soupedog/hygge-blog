import React from 'react';
import {Avatar, Dropdown, Menu, MenuProps, message} from "antd";
import {CloseCircleOutlined, EditOutlined} from "@ant-design/icons";
import {UrlHelper} from "../../utils/UtilContainer";
import {UserService} from "../../rest/ApiClient";
import {useParams} from "react-router-dom";

function HyggeUserMenu() {
    const user = UserService.getCurrentUser();

    if (user == null) {
        return null;
    }

    const {aid} = useParams();

    return (
        // useParams 要求是 hook 组件，而新版本 menu 方式不是
        <Dropdown overlay={
            <Menu items={items} onClick={(event) => {
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
                            UrlHelper.openNewPage({path: "editor/article/" + aid, inNewTab: false})
                        } else {
                            UrlHelper.openNewPage({path: "editor/article", inNewTab: false})
                        }
                        break;
                    case "editQuote":
                        UrlHelper.openNewPage({path: "editor/quote", inNewTab: false})
                        break;
                }
            }}/>
        }>
            <Avatar className={"pointer"} size={48} src={user.userAvatar}/>
        </Dropdown>
    );
}

const items: MenuProps['items'] = [
    {
        label: '编辑文章',
        key: 'editArticle',
        icon: <EditOutlined/>,
    },
    {
        label: '编辑句子收藏',
        key: 'editQuote',
        icon: <EditOutlined/>,
    },
    {
        label: '登出',
        key: 'signOut',
        icon: <CloseCircleOutlined/>,
    },
];

// 新版本 menu 模式(无法正确获取 aid)
const onClick: MenuProps['onClick'] = ({key}) => {
    let aid = null;
    let finalUrl = UrlHelper.getBaseUrl();
    switch (key) {
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
                UrlHelper.openNewPage({path: "editor/article/" + aid, inNewTab: false})
            } else {
                UrlHelper.openNewPage({path: "editor/article", inNewTab: false})
            }
            break;
        case "editQuote":
            UrlHelper.openNewPage({path: "editor/quote", inNewTab: false})
            break;
    }
};

export default HyggeUserMenu;