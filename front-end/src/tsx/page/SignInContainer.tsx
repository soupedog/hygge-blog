import * as React from "react"
import {LogHelper, UrlHelper} from '../utils/UtilContainer';

import zhCN from "antd/lib/locale/zh_CN";
import {LockOutlined, UserOutlined} from '@ant-design/icons';
import {Button, ConfigProvider, Form, Input, Layout} from "antd";
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
                                UserService.signIn(val.uid, val.password, (data) => {
                                    console.log(data);

                                    UrlHelper.openNewPage({inNewTab: false});
                                });
                                // LoginService.login({uid: val.uid, password: val.password})
                            }}
                        >
                            <Form.Item
                                name="uid"
                                rules={[{required: true, message: '请输入 UID!'}]}
                            >
                                <Input prefix={<UserOutlined className="site-form-item-icon"/>} placeholder="UID"/>
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