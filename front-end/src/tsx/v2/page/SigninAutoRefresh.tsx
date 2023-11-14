import React from 'react';
import {UserService} from "../../rest/ApiClient";
import {message} from "antd";
import {UrlHelper} from "../../utils/UtilContainer";

function SigninAutoRefresh() {
    // 不传入参是用本地身份信息刷新令牌
    UserService.signIn(undefined, undefined, (data) => {
        if (data?.code == 200) {
            message.info("已为您成功自动登录，1 秒内为您跳转回主页", 1);
            UrlHelper.openNewPage({inNewTab: false, delayTime: 1000});
        } else {
            message.info("自动登录失败，1 秒内为您跳转回登录页", 1);
            // 刷新秘钥自动登录失败，需要清空本地身份信息
            UserService.removeCurrentUser();
            UrlHelper.openNewPage({inNewTab: false, path: "signin", delayTime: 1000});
        }
    });
    return (
        <>
        </>
    );
}

export default SigninAutoRefresh;