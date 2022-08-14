import * as React from "react"
import {LogHelper, PropertiesHelper, UrlHelper} from '../utils/UtilContainer';
import {FileService, HyggeResponse, QuoteDto, QuoteService, UserService} from "../rest/ApiClient";
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
    Space
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
    quoteId?: string;
    currentQuote?: QuoteDto;
    backgroundImageData?: SelectProps['options'];
    mdController?: Vditor;
}

class EditQuoteContainer extends React.Component<EditQuoteContainerProps, EditQuoteContainerState> {
    formRef = React.createRef<FormInstance>();

    constructor(props: EditQuoteContainerProps) {
        super(props);
        this.state = {
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
                                value.content = _react.state.mdController?.getValue();

                                if (!PropertiesHelper.isStringNotEmpty(value.imageSrc)) {
                                    value.imageSrc = null;
                                }

                                if (!PropertiesHelper.isStringNotEmpty(value.remarks)) {
                                    value.remarks = null;
                                }

                                if (!PropertiesHelper.isStringNotEmpty(value.source)) {
                                    value.source = null;
                                }

                                if (!PropertiesHelper.isStringNotEmpty(value.portal)) {
                                    value.portal = null;
                                }

                                if (value.action == "update") {
                                    let quoteId = value.quoteId + "";
                                    if (PropertiesHelper.isStringNotEmpty(quoteId)) {
                                        QuoteService.updateQuote(quoteId, value, () => {
                                                message.success("修改句子成功");
                                            }
                                        );
                                    } else {
                                        message.warn("修改句子时 quoteId 不可为空");
                                    }
                                } else if (value.action == "add") {
                                    QuoteService.createQuote(value, (data) => {
                                            _react.updateForm(data!, _react);
                                            message.success("添加句子成功");
                                        }
                                    );
                                }
                                console.log(value);
                            }}
                        >
                            <Form.Item name={['quoteId']} label="句子编号" rules={[{required: false}]}>
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
                            <Form.Item name={['quoteState']} label="句子状态"
                                       rules={[{required: true}]}
                                       initialValue={"ACTIVE"}
                            >
                                <Radio.Group>
                                    <Radio value={"ACTIVE"}>启用</Radio>
                                    <Radio value={"INACTIVE"}>禁用</Radio>
                                </Radio.Group>
                            </Form.Item>
                            <Form.Item style={{display: "none"}} name={['action']} label="操作类型"
                                       rules={[{required: true}]}
                                       initialValue={"add"}>
                                <Radio.Group>
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
                                        _react.fetchQuote(_react);
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
        document.title = "句子编辑器";
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
                _react.setState({mdController: vditor});

                _react.fetchQuote(_react);
            },
        });

        this.updateFileInfo();
    }

    fetchQuote(_react: this) {
        let quoteId = _react.props.router.params.quoteId;
        if (PropertiesHelper.isStringNotEmpty(quoteId)) {
            QuoteService.findQuoteByQuoteId(quoteId, (data) => {
                if (data != null) {
                    _react.updateRootStatus({
                        currentQuote: data.main
                    })

                    if (_react.state.mdController != null && data.main != null) {
                        // 更新 MD 编辑器
                        _react.state.mdController.setValue(data.main.content);
                        _react.updateForm(data, _react);
                    }

                    message.info("句子信息已尝试拉取");
                }
            });
        }
    }

    updateFileInfo() {
        let _react = this;
        let type = ["QUOTE"]

        let container: SelectProps['options'] = [];

        FileService.findFileInfo(type, (data) => {
            data?.main?.forEach((item) => {
                container?.push({
                    label: item.name + " --- " + item.fileSize + " mb",
                    value: UrlHelper.getBaseStaticSourceUrl() + item.src,
                });
            });

            _react.updateRootStatus({backgroundImageData: container});

            message.info("封面已尝试拉取");
        });
    }

    updateForm(data: HyggeResponse<QuoteDto>, _react: this) {
        console.log(data)

        let quoteDto: QuoteDto = data.main!;
        _react.formRef.current?.setFieldsValue({
            quoteId: quoteDto.quoteId,
            imageSrc: quoteDto.imageSrc,
            remarks: quoteDto.remarks,
            source: quoteDto.source,
            portal: quoteDto.portal,
            quoteState: quoteDto.quoteState,
        });
    }

    updateRootStatus(deltaInfo: EditQuoteContainerState) {
        this.setState(deltaInfo);
    }
}

export default withRouter(EditQuoteContainer)