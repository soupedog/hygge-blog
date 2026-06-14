import {useEffect} from 'react';
import {Button, Form, Input, Layout, Space} from 'antd';
import {Content} from 'antd/es/layout/layout';
import SimpleHeader from './component/SimpleHeader.tsx';
import {LockOutlined, UserOutlined} from '@ant-design/icons';
import AppFooter from './component/AppFooter.tsx';
import {useUserService} from '../util/ApiService.ts';
import UrlHelper from '../util/UrlHelper.ts';

interface FieldType {
    username?: string;
    password?: string;
}

export default function Signin() {
    const [form] = Form.useForm();
    const {signIn} = useUserService();

    const handleLogin = (values: any) => {
        // 再次提供 onSuccess 并不是覆盖，而是包括 useUserService 内已经定义的共计两个方法都会执行
        // 顺序是先默认再此处 onSuccess
        signIn.mutate({ac: values.username, pw: values.password},
            {
                onSuccess: (data) => {
                    UrlHelper.navigateTo({path: '/', canBack: false});
                }
            }
        );
    };

    useEffect(() => {
        // 依赖静态值表示仅初始化时调用一次
        document.title = '登录页 | 我的小宅子';
    }, []);

    return (
        <Layout>
            <SimpleHeader title={'登录'}/>
            <Content style={{minHeight: '35rem'}}>
                <Form
                    name='basic'
                    form={form}
                    onFinish={(values) => {
                        handleLogin(values);
                    }}
                    // 不再记录历史信息
                    autoComplete='off'
                >
                    <div style={{width: '20rem', margin: '8rem auto 2rem auto'}}>
                        <Form.Item<FieldType>
                            name='username'
                            rules={[{required: true, message: '请输入账号！'}]}
                        >
                            <Input prefix={<UserOutlined/>} placeholder='账号'/>
                        </Form.Item>
                        <Form.Item<FieldType>
                            name='password'
                            rules={[{required: true, message: '请输入密码！'}]}
                        >
                            <Input prefix={<LockOutlined/>} type='password' placeholder='密码'/>
                        </Form.Item>
                    </div>
                    <Form.Item label={null} wrapperCol={{offset: 12, span: 12}}>
                        <Space size={'large'}>
                            <Button htmlType='button' onClick={() => {
                                form.resetFields()
                            }}>
                                重置
                            </Button>
                            <Button type='primary' htmlType='submit'>
                                登录
                            </Button>
                        </Space>
                    </Form.Item>
                </Form>
            </Content>
            <AppFooter/>
        </Layout>
    );
}
