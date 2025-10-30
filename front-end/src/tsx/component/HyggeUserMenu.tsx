import React from 'react';
import {Avatar, Dropdown, MenuProps, message} from "antd";
import {CloseCircleOutlined, EditOutlined, PaperClipOutlined} from "@ant-design/icons";
import {UserService} from "../rest/ApiClient";
import {useParams} from "react-router-dom";
import {UrlHelper} from "../util/UtilContainer";

function HyggeUserMenu() {
    const user = UserService.getCurrentUser();

    if (user == null) {
        return null;
    }

    const {aid} = useParams();

    items?.forEach(item => {
        let keyOfItem = item!.key as string;
        let index = keyOfItem.indexOf(",");

        if (index == -1 && aid != undefined) {
            // 找不到则添加
            item!.key = keyOfItem + "," + aid;
        }
    })

    return (
        // useParams 要在 hook 组件内用，而新版本 menu 数据定义在 hook 组件外
        <Dropdown menu={{items, onClick}}>
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
        label: '文件管理',
        key: 'fileManage',
        icon: <PaperClipOutlined/>,
    },
    {
        label: '文件操作',
        key: 'fileOperation',
        icon: <PaperClipOutlined/>,
    },
    {
        label: '登出',
        key: 'signOut',
        icon: <CloseCircleOutlined/>,
    },
];

// 新版本 menu 模式(无法正确获取 aid)
// workaround，把 aid 赋进 key，后续再截取 key
const onClick: MenuProps['onClick'] = ({key}) => {
    let keyInfo = key.split(",");
    let actualKay = keyInfo[0];
    let aid = keyInfo[1];
    let finalUrl = UrlHelper.getBaseUrl();
    switch (actualKay) {
        case "editArticle":
            if (keyInfo.length > 1) {
                UrlHelper.openNewPage({path: "editor/article/" + aid, inNewTab: true})
            } else {
                UrlHelper.openNewPage({path: "editor/article", inNewTab: true})
            }
            break;
        case "editQuote":
            UrlHelper.openNewPage({path: "editor/quote", inNewTab: true})
            break;
        case "fileManage":
            UrlHelper.openNewPage({path: "file/manage", inNewTab: true})
            break;
        case "fileOperation":
            UrlHelper.openNewPage({path: "file/operation", inNewTab: true})
            break;
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
    }
};

export default HyggeUserMenu;