import * as React from "react"
import {LogHelper, PropertiesHelper, UrlHelper} from '../utils/UtilContainer';
import {
    AllOverviewInfo,
    FileService,
    HomePageService,
    HyggeResponse,
    UserService
} from "../rest/ApiClient";
import {ReactRouter, withRouter} from "../utils/ReactRouterHelper";
import zhCN from "antd/lib/locale/zh_CN";
import {
    Button,
    ConfigProvider,
    Form,
    FormInstance,
    Input,
    Layout,
    message,
    Radio,
    Select,
    SelectProps,
    Space,
    TreeSelect
} from "antd";
import {HyggeFooter} from "./component/HyggeFooter";

import "./../../css/editQuote.less"
import "vditor/dist/index.css"
import Vditor from "vditor";

const {Header, Content} = Layout;

// 描述该组件 props 数据类型
export interface EditQuoteContainerProps {
    router: ReactRouter;
}

// 描述该组件 states 数据类型
export interface EditQuoteContainerState {
    aid?: string;
    backgroundMusicType?: string;
    categoryTreeData?: any[];
    backgroundImageData?: SelectProps['options'];
    mdController?: Vditor;
}

class EditQuoteContainer extends React.Component<EditQuoteContainerProps, EditQuoteContainerState> {
    formRef = React.createRef<FormInstance>();

    constructor(props: EditQuoteContainerProps) {
        super(props);
        this.state = {
            backgroundMusicType: "NONE",
            categoryTreeData: [],
            backgroundImageData: []
        };

        let currentUser = UserService.getCurrentUser();
        if (currentUser == null) {
            message.warn("未登录用户无权访问，2 秒内自动为您跳转至主页")
            UrlHelper.openNewPage({inNewTab: false, delayTime: 2000})
        }
        LogHelper.info({className: "EditQuoteContainer", msg: "初始化成功"});
    }

    render() {
        let _react = this;
        return (
            <ConfigProvider locale={zhCN}>
                <Layout className="layout">
                    <Header>
                        <div className="page-title floatToLeft" style={{width: 200}}>我的小宅子---句子编辑</div>
                    </Header>
                    <Content className="mainContent"
                             style={{padding: '0 50px', minHeight: window.innerHeight - 182}}>
                        <div id="preview" style={{marginTop: "10px", marginBottom: "20px"}}/>
                        <Form
                            ref={this.formRef}
                            name="hygge_login"
                            style={{
                                maxWidth: "80%",
                                margin: "40px auto 0 auto"
                            }}
                            onFinish={(value) => {
                             
                                console.log(value);
                            }}
                        >
                            <Form.Item name={['aid']} label="句子编号" rules={[{required: false}]}>
                                <Input/>
                            </Form.Item>
                            <Form.Item name={['imageSrc']} label="句子配图"
                                       rules={[{required: false}]}>
                                <Select
                                    showSearch
                                    placeholder="请选择背景图片"
                                    optionFilterProp="children"
                                    options={this.state.backgroundImageData}
                                >
                                </Select>
                            </Form.Item>
                            <Form.Item name={['remarks']} label="备注" rules={[{required: false}]}>
                                <Input.TextArea rows={2}/>
                            </Form.Item>
                            <Form.Item name={['source']} label="出处" rules={[{required: false}]}>
                                <Input.TextArea rows={2}/>
                            </Form.Item>
                            <Form.Item name={['portal']} label="传送门" rules={[{required: false}]}>
                                <Input/>
                            </Form.Item>
                            <Form.Item style={{display: "none"}} name={['action']} label="操作类型"
                                       rules={[{required: true}]}
                                       initialValue={"add"}>
                                <Radio.Group onChange={(event) => {
                                    this.setState({backgroundMusicType: event.target.value});
                                }}>
                                    <Radio value={"add"}>添加句子</Radio>
                                    <Radio value={"update"}>修改句子</Radio>
                                </Radio.Group>
                            </Form.Item>
                            <Form.Item>
                                <Space size={"large"} align={"end"}>
                                    <Button type="primary" htmlType="submit" onClick={() => {
                                        _react.formRef.current?.setFieldsValue({
                                            action: 'add'
                                        });
                                    }}>
                                        添加句子
                                    </Button>
                                    <Button type="primary" htmlType="submit" danger onClick={() => {
                                        _react.formRef.current?.setFieldsValue({
                                            action: 'update'
                                        });
                                    }}>
                                        修改句子
                                    </Button>
                                    <Button type="ghost" htmlType="button" onClick={() => {
                                    }}>
                                        查询句子
                                    </Button>
                                </Space>
                            </Form.Item>
                        </Form>
                    </Content>
                    <HyggeFooter/>
                </Layout>
            </ConfigProvider>
        );
    }

    componentDidMount() {
        let _react = this;
        let vditor = new Vditor('preview', {
            // cdn: "https://www.xavierwang.cn/static/npm/vditor@3.8.5",
            height: 300,
            mode: "sv",
            value: "",
            toolbarConfig: {
                pin: true,
            },
            preview: {
                hljs: {
                    style: "native",
                    lineNumber: true
                },
            },
            cache: {
                enable: false,
            },
            after() {
                // 更新 MD 控制器
            },
        });
    }

    updateRootStatus(deltaInfo: EditQuoteContainerState) {
        this.setState(deltaInfo);
    }
}

export default withRouter(EditQuoteContainer)