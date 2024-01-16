import React, {useEffect, useState} from 'react';
import {Button, Tooltip} from "antd";
import {RollbackOutlined} from "@ant-design/icons";
import {Header} from "antd/es/layout/layout";
import HyggeUserMenu from "../HyggeUserMenu";
import {useNavigate, useSearchParams} from "react-router-dom";
import clsx from "clsx";
import {WindowsEventHelper} from "../../../utils/UtilContainer";
import {UserService} from "../../../rest/ApiClient";

export interface HyggeBrowserHeaderState {
    headerTransparent: Boolean;
}

function HyggeBrowserHeader() {
    const navigate = useNavigate();
    const [searchParams, setSearchParams] = useSearchParams();
    const [headerTransparent, updateHeaderTransparent] = useState(false);

    useEffect(() => {
        WindowsEventHelper.addCallback_Scroll({
            name: "APPBar 透明判定", delta: 50, callbackFunction: function ({currentScrollY}) {
                if (currentScrollY > 336) {
                    updateHeaderTransparent(false);
                } else {
                    updateHeaderTransparent(true);
                }
            }
        });
        WindowsEventHelper.start_OnScroll();
        // 依赖静态值表示仅初始化时调用一次
    }, []);

    return (
        <Header style={{position: 'fixed', zIndex: 999, width: '100%'}}
                className={clsx({
                    "backgroundTransparent": headerTransparent
                })}>
            <div className={"floatToLeft"}>
                <Tooltip placement="bottom" title={"返回首页"}>
                    <RollbackOutlined onClick={() => {
                        let finalPath = "/";
                        if (searchParams.has("secretKey")) {
                            finalPath = finalPath + "?secretKey=" + searchParams.get("secretKey")
                        }
                        navigate(finalPath);
                    }} style={{color: "#fff", fontWeight: "bold", fontSize: "24px", lineHeight: "64px"}}/>
                </Tooltip>
            </div>
            <div className={"floatToRight"}>
                {UserService.getCurrentUser() != null ? <HyggeUserMenu/> :
                    <Button type="primary" onClick={() => {
                        navigate("/signin");
                    }}>
                        登录
                    </Button>}
            </div>
        </Header>
    );
}

export default HyggeBrowserHeader;