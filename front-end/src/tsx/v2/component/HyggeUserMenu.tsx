import React from 'react';
import {Avatar, Dropdown, MenuProps, message} from "antd";
import {CloseCircleOutlined, EditOutlined} from "@ant-design/icons";
import {UrlHelper} from "../../utils/UtilContainer";
import {UserService} from "../../rest/ApiClient";

function HyggeUserMenu() {
    // 渲染该组件前，外部需判断当前用户不为空
    return (
        <Dropdown menu={{items, onClick}} trigger={['click']}>
            <Avatar className={"pointer"} size={48} src={UserService.getCurrentUser()?.userAvatar}/>
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