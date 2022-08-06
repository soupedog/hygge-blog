import * as React from "react"
import {LogHelper, UrlHelper} from '../utils/UtilContainer';

import zhCN from "antd/lib/locale/zh_CN";
import {LockOutlined, UserOutlined} from '@ant-design/icons';
import {Button, ConfigProvider, Form, Input, Layout, message} from "antd";
import {HyggeFooter} from "./component/HyggeFooter";

import "./../../css/signin.less"
import {UserService} from "../rest/ApiClient";

const {Header, Content} = Layout;

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
        UserService.removeCurrentUser()
        LogHelper.info({className: "SignInContainer", msg: "初始化成功"});
    }

    render() {
        return (
            <ConfigProvider locale={zhCN}>
                <Layout className="layout">
                    <Header>
                        <div className="page-title floatToLeft" style={{width: 200}}>我的小宅子---登录页面</div>
                    </Header>
                    <Content className="mainContent" style={{padding: '0 50px', minHeight: window.innerHeight - 182}}>
                        <Form
                            name="hygge_login"
                            className="login-form"
                            onFinish={(val) => {
                                UserService.signIn(val.account, val.password, (data) => {
                                    message.success("登录成功，1 秒内自动跳转回主页，请稍后", 1000);
                                    UrlHelper.openNewPage({inNewTab: false, delayTime: 1000});
                                });
                            }}
                        >
                            <Form.Item
                                name="account"
                                rules={[{required: true, message: '请输账号!'}]}
                            >
                                <Input prefix={<UserOutlined className="site-form-item-icon"/>} placeholder="账号"/>
                            </Form.Item>
                            <Form.Item
                                name="password"
                                rules={[{required: true, message: '请输入密码!'}]}
                            >
                                <Input
                                    prefix={<LockOutlined className="site-form-item-icon"/>}
                                    type="password"
                                    placeholder="密码"
                                />
                            </Form.Item>
                            <Form.Item>
                                <Button type="primary" htmlType="submit" className="login-form-button">
                                    登录
                                </Button>
                            </Form.Item>
                        </Form>
                    </Content>
                    <HyggeFooter/>
                </Layout>
            </ConfigProvider>
        );
    }
}