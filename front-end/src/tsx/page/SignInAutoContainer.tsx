import * as React from "react"
import {LogHelper, UrlHelper} from '../utils/UtilContainer';
import {message} from "antd";

import "./../../css/signin.less"
import {UserService} from "../rest/ApiClient";


// 描述该组件 props 数据类型
export interface SignInAutoContainerProps {
}

// 描述该组件 states 数据类型
export interface SignInAutoContainerStatus {
}

export class SignInAutoContainer extends React.Component<SignInAutoContainerProps, SignInAutoContainerStatus> {
    constructor(props: SignInAutoContainerProps) {
        super(props);
        this.state = {};
        LogHelper.info({className: "SignInAutoContainer", msg: "初始化成功"});
    }

    render() {
        return (
            <></>
        );
    }

    componentDidMount() {
        UserService.signIn(undefined, undefined, (data) => {
            if (data?.code == 200) {
                message.info("已为您成功自动登录，1 秒内为您跳转回主页", 1);
                UrlHelper.openNewPage({inNewTab: false, delayTime: 1000});
            } else {
                message.info("自动登录失败，1 秒内为您跳转回登录页", 1);
                UrlHelper.openNewPage({inNewTab: false, path: "#/signin", delayTime: 1000});
            }
        });
    }
}