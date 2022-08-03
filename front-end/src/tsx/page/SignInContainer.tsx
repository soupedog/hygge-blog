import * as React from "react"
import {LogHelper} from '../utils/UtilContainer';

// 描述该组件 props 数据类型
export interface SignInContainerProps {
}

// 描述该组件 states 数据类型
export interface SignInContainerStatus {
}

export class SignInContainer extends React.Component<SignInContainerProps, SignInContainerStatus> {
    constructor(props: SignInContainerProps) {
        super(props);
        this.state = {};
        LogHelper.info({className: "SignInContainer", msg: "初始化成功"});
    }

    render() {
        return (
            <>
                <h1>登录</h1>
            </>
        );
    }
}