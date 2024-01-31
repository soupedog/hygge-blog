import React, {useEffect} from 'react';

import "../../style/signin.less"

import {Button, ConfigProvider, Form, Input, Layout, message} from "antd";
import {UserService} from "../rest/ApiClient";
import {LockOutlined, UserOutlined} from "@ant-design/icons";
import HyggeFooter from "../component/HyggeFooter";
import {Content, Header} from "antd/es/layout/layout";
import {
    class_index_title,
    class_signin_form,
    class_signin_form_submit
} from "../component/properties/ElementNameContainer";
import zhCN from "antd/lib/locale/zh_CN";
import {UrlHelper} from "../util/UtilContainer";

function SignIn() {

    useEffect(() => {
        // 改页面标题
        document.title = "登录页";
        // 依赖静态值表示仅初始化时调用一次
    }, []);

    return (
        <ConfigProvider locale={zhCN}>
            <Layout className="layout">
                <Header>
                    <div className={class_index_title + " floatToLeft"} style={{width: 200}}>我的小宅子---登录</div>
                </Header>
                <Content style={{padding: '0 50px', minHeight: window.innerHeight - 226}}>
                    <Form
                        name="hygge_login"
                        className={class_signin_form}
                        onFinish={(val) => {
                            UserService.signIn(val.account, val.password, () => {
                                localStorage.removeItem('autoRefreshDisableFlag');
                                message.success("登录成功，1 秒内自动跳转回主页，请稍后", 1000);
                                // this.props.router.navigate("/signin");
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
                            <Button type="primary" htmlType="submit" className={class_signin_form_submit}>
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

export default SignIn;