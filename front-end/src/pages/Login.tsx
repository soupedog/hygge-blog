import {useEffect} from 'react';
import {Button, Checkbox, Form, Input, Space} from "antd";
import {useNavigate} from "react-router-dom";

export interface LoginProps {
    readonly text: string
}

interface FieldType {
    username?: string;
    password?: string;
    remember?: string;
}

export default function Login() {
    const [form] = Form.useForm();
    const navigate = useNavigate();

    useEffect(() => {
        // 依赖静态值表示仅初始化时调用一次
        document.title = "登录页";
    }, []);

    return (
        <div className={"fullPage"}>
            <Form
                name="basic"
                labelCol={{span: 8}}
                wrapperCol={{span: 16}}
                style={{width: 600, margin: "0 auto"}}
                initialValues={{remember: true}}
                form={form}
                onFinish={(values) => {
                    console.log(values);
                }}
                onFinishFailed={(errorInfo) => {
                    console.log(errorInfo);
                }}
                autoComplete="off"
            >
                <Form.Item<FieldType>
                    label="Username"
                    name="username"
                    rules={[{required: true, message: 'Please input your username!'}]}
                >
                    <Input/>
                </Form.Item>

                <Form.Item<FieldType>
                    label="Password"
                    name="password"
                    rules={[{required: true, message: 'Please input your password!'}]}
                >
                    <Input.Password/>
                </Form.Item>

                <Form.Item<FieldType> name="remember" valuePropName="checked" label={null}>
                    <Checkbox>Remember me</Checkbox>
                </Form.Item>

                <Form.Item label={null} wrapperCol={{offset: 8, span: 16}}>
                    <Space>
                        <Button type="primary" htmlType="submit">
                            Submit
                        </Button>
                        <Button htmlType="button" onClick={() => {
                            form.resetFields()
                        }}>
                            Reset
                        </Button>
                        <Button type="link" htmlType="button" onClick={() => {
                            form.setFieldsValue({username: 'Hello world!', password: 'male'});
                        }}>
                            Fill form
                        </Button>
                        <Button type="primary" onClick={() => {
                            // replace : false 该跳转不要记录(跳转后无法通过后退键返回跳转前的页面)
                            navigate("/", {replace: false});
                        }}>
                            Back To Home Page
                        </Button>
                    </Space>
                </Form.Item>
            </Form>
        </div>
    );
}
