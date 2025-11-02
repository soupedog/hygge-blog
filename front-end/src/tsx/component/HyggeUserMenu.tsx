import React from 'react';
import {Avatar, Dropdown, MenuProps, message} from "antd";
import {CloseCircleOutlined, EditOutlined, PaperClipOutlined} from "@ant-design/icons";
import {UserService} from "../rest/ApiClient";
import {useNavigate, useParams} from "react-router-dom";
import {UrlHelper} from "../util/UtilContainer";

function HyggeUserMenu() {
    const navigate = useNavigate();
    const {aid} = useParams();
    const user = UserService.getCurrentUser();

    const onClick: MenuProps['onClick'] = ({key}) => {
        let currentSecretKey = UrlHelper.getQueryString("secretKey");
        let queryString = "";

        if (currentSecretKey != null) {
            queryString = "?secretKey=" + currentSecretKey;
        }

        switch (key) {
            case "editArticle":
                if (aid && aid.length > 1) {
                    navigate("/editor/article/" + aid + queryString);
                } else {
                    navigate("/editor/article" + queryString);
                }
                break;
            case "editQuote":
                navigate("/editor/quote" + queryString);
                break;
            case "fileManage":
                navigate("/file/manage" + queryString);
                break;
            case "fileOperation":
                navigate("/file/operation" + queryString);
                break;
            case "signOut":
                UserService.removeCurrentUser();
                message.success("登出成功，1 s 后将跳转回首页。");
                navigate("/" + queryString);
                break;
        }
    };

    return (
        <Dropdown menu={{items, onClick}}>
            <Avatar className={"pointer"} size={48} src={user?.userAvatar}/>
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


export default HyggeUserMenu;