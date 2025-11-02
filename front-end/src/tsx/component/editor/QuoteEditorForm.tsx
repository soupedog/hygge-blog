import React, {useEffect, useState} from 'react';
import {Button, Form, Input, Layout, message, Radio, Select, SelectProps, Space} from "antd";
import {Content} from "antd/es/layout/layout";
import {useParams} from "react-router-dom";
import {FileService, HyggeResponse, QuoteDto, QuoteService} from "../../rest/ApiClient";
import {FormInstance} from "antd/es/form/hooks/useForm";
import {PropertiesHelper, UrlHelper} from "../../util/UtilContainer";
import {QuoteEditorContext} from '../../page/QuoteEditor';
import HyggeFooter from "../HyggeFooter";


function QuoteEditorForm({updateContent}: { updateContent: Function }) {
    const [quoteForm] = Form.useForm();
    const {quoteId} = useParams();

    const [imageInfoList, updateImageInfoList] = useState([]);

    useEffect(() => {
        refreshImageInfoList(updateImageInfoList);

        if (quoteId) {
            QuoteService.findQuoteByQuoteId(quoteId, (data) => {
                if (data?.main == null) {
                    message.warning("目标句子不存在", 2000);
                } else {
                    message.info("句子信息已尝试拉取");
                    refreshFormInfo(updateContent, data, quoteForm);
                }
            });
        }
        // 依赖静态值表示仅初始化时调用一次
    }, []);

    return (
        <QuoteEditorContext.Consumer>
            {({content}) => (
                <Layout className="layout">
                    {/*<Header>*/}
                    {/*    <div className="page-title floatToLeft" style={{width: 200}}>我的小宅子---句子编辑</div>*/}
                    {/*</Header>*/}
                    <Content className="mainContent"
                             style={{padding: '0 50px', minHeight: window.innerHeight - 182}}>
                        <div id="preview" style={{marginTop: "10px", marginBottom: "20px"}}/>
                        <Form
                            // 不再记录历史信息
                            autoComplete={"off"}
                            form={quoteForm}
                            name="hygge_quote"
                            style={{
                                maxWidth: "80%",
                                margin: "40px auto 0 auto"
                            }}
                            onFinish={(value) => {
                                value.content = content;

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

                                console.log(value);

                                if (value.action == "update") {
                                    let quoteId: string = value.quoteId + "";
                                    if (PropertiesHelper.isStringNotEmpty(quoteId)) {
                                        QuoteService.updateQuote(quoteId, value, () => {
                                                message.success("修改句子成功");
                                            }
                                        );
                                    } else {
                                        message.warning("修改句子时 quoteId 不可为空");
                                    }
                                } else if (value.action == "add") {
                                    QuoteService.createQuote(value, (data) => {
                                            refreshFormInfo(updateContent, data!, quoteForm);
                                            message.success("添加句子成功");
                                        }
                                    );
                                }
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
                                    options={imageInfoList}
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
                                        quoteForm.setFieldsValue({
                                            action: 'add'
                                        });
                                    }}>
                                        添加句子
                                    </Button>
                                    <Button type="primary" htmlType="submit" danger onClick={() => {
                                        quoteForm.setFieldsValue({
                                            action: 'update'
                                        });
                                    }}>
                                        修改句子
                                    </Button>
                                    <Button type="dashed" htmlType="button" onClick={() => {
                                        // _react.fetchQuote(_react);
                                    }}>
                                        查询句子
                                    </Button>
                                </Space>
                            </Form.Item>
                        </Form>
                    </Content>
                    <HyggeFooter/>
                </Layout>
            )}
        </QuoteEditorContext.Consumer>
    );

    function refreshImageInfoList(updateBackgroundImageInfoList: Function) {
        let type = ["QUOTE"];

        let container: SelectProps['options'] = [];

        FileService.findFileInfoMulti(type, (data) => {
            data?.main?.fileInfoList.forEach((item) => {
                container?.push({
                    label: item.name + " --- " + item.fileSize,
                    value: UrlHelper.getBaseStaticSourceUrl() + item.src,
                });
            });
            updateBackgroundImageInfoList(container);
            message.info("封面已尝试拉取");
        });
    }

    function refreshFormInfo(updateContent: Function, data: HyggeResponse<QuoteDto>, form: FormInstance<any>) {
        let quoteDto: QuoteDto = data.main!;

        updateContent(quoteDto.content);

        quoteForm.setFieldsValue({
            quoteId: quoteDto.quoteId,
            imageSrc: quoteDto.imageSrc,
            remarks: quoteDto.remarks,
            source: quoteDto.source,
            portal: quoteDto.portal,
            quoteState: quoteDto.quoteState,
        });
    }
}

export default QuoteEditorForm;