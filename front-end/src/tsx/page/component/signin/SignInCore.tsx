import * as React from "react"
import {LogHelper} from '../../../utils/UtilContainer';
import {Layout} from "antd";

// 描述该组件 props 数据类型
export interface SignInCoreProps {
}

// 描述该组件 states 数据类型
export interface SignInCoreStatus {
}

export class SignInCore extends React.Component<SignInCoreProps, SignInCoreStatus> {
    constructor(props: SignInCoreProps) {
        super(props);
        this.state = {};
        LogHelper.info({className: "SignInCore", msg: "初始化成功"});
    }

    render() {
        return (
            <Layout>
            </Layout>
        );
    }
}
