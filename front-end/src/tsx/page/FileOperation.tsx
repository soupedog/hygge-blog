import "../../style/fileOperation.less"

import React, {useEffect, useState} from 'react';
import {
    Button,
    Col,
    ConfigProvider,
    DatePicker,
    Form,
    Input,
    Layout,
    message,
    Radio,
    Row,
    Select,
    TreeSelect,
    Upload
} from "antd";
import zhCN from "antd/lib/locale/zh_CN";
import {Content, Header} from "antd/es/layout/layout";
import {class_file_operation_form, class_index_title} from "../component/properties/ElementNameContainer";
import {LogHelper, PropertiesHelper, TimeHelper, TimeType, UrlHelper} from "../util/UtilContainer";
import HyggeFooter from "../component/HyggeFooter";
import {DefaultOptionType} from "rc-select/lib/Select";
import {UploadOutlined} from "@ant-design/icons";
import {AllOverviewInfo, FileInfo, FileService, HomePageService, UserService} from "../rest/ApiClient";
import TextArea from "antd/es/input/TextArea";
import dayjs from "dayjs";
import {useParams} from "react-router-dom";

function FileOperation() {
    const [categoryInfoList, updateCategoryInfoList] = useState([]);
    const [actualUrl, updateActualUrl] = useState("");
    const [fileNoRequired, updateFileNoRequired] = useState(true);
    const [fileOperationForm] = Form.useForm();
    const {fileNo} = useParams();

    useEffect(() => {
        // 改页面标题
        document.title = "文件操作";

        refreshCategoryInfoList(updateCategoryInfoList);
        // 依赖静态值表示仅初始化时调用一次
    }, []);

    return (
        <ConfigProvider locale={zhCN}>
            <Layout className="layout">
                <Header>
                    <div className={class_index_title + " floatToLeft"} style={{width: 200}}>我的小宅子---文件操作</div>
                </Header>
                <Content style={{padding: '0 50px', minHeight: window.innerHeight - 226}}>
                    <Form
                        // 不再记录历史信息
                        autoComplete={"off"}
                        form={fileOperationForm}
                        name="hygge_file_operation"
                        className={class_file_operation_form}
                        onFinish={(value) => {
                            if (value.action == "query") {
                                FileService.findFileInfo(value.fileNo, (data) => {
                                    if (data?.main != null) {
                                        refreshFormData(data.main)
                                    }
                                    message.info("查询文件信息完毕。")
                                })
                            } else if (value.action == "update") {
                                FileService.updateFileInfo(value,()=>{
                                    message.success("文件信息更新成功。")
                                });
                            }
                        }}
                    >
                        <Form.Item name={['fileNo']} label="文件编号"
                                   rules={[{required: fileNoRequired}]}
                        initialValue={fileNo}>
                            <Input/>
                        </Form.Item>
                        <Row justify="center" gutter={[24, 16]}>
                            <Col span={19}>
                                <Form.Item name={['name']} label="文件名称" rules={[{required: false}]}>
                                    <Input/>
                                </Form.Item>
                            </Col>
                            <Col span={5}>
                                <Form.Item name={['extension']} label="扩展名" rules={[{required: false}]}>
                                    <Input/>
                                </Form.Item>
                            </Col>
                        </Row>
                        <Row justify="center" gutter={[24, 16]}>
                            <Col span={8}>
                                <Form.Item name={['fileType']} label="文件类型" rules={[{required: true}]}
                                           initialValue="OTHERS">
                                    <Select
                                        style={{width: 120}}
                                        options={selectItems}
                                        placeholder="请选择文件类型"
                                    />
                                </Form.Item>
                            </Col>
                            <Col span={4}></Col>
                            <Col span={12}>
                                <Form.Item name={['cid']} label="所属文章类型" rules={[{required: false}]}>
                                    <TreeSelect
                                        style={{width: '100%'}}
                                        styles={{
                                            popup: {root: {maxHeight: 400, overflow: 'auto'}},
                                        }}
                                        treeData={categoryInfoList}
                                        placeholder="请选择文章类型"
                                        treeDefaultExpandAll
                                        onChange={() => {
                                        }}
                                    />
                                </Form.Item>
                            </Col>
                        </Row>
                        <Form.Item name={['description', 'timePointer']} label="图片发生时间"
                                   rules={[{required: false}]}>
                            <DatePicker showTime format={"YYYY-MM-DD HH:mm:ss"}/>
                        </Form.Item>
                        <Form.Item name={['description', 'content']} label="文件备注" rules={[{required: false}]}>
                            <TextArea/>
                        </Form.Item>
                        <Form.Item style={{display: "none"}} name={['action']} label="操作类型"
                                   rules={[{required: true}]}
                                   initialValue={"query"}>
                            <Radio.Group>
                                <Radio value={"query"}>查询文件</Radio>
                                <Radio value={"update"}>修改文件</Radio>
                                <Radio value={"upload"}>上传文件</Radio>
                            </Radio.Group>
                        </Form.Item>
                        <Form.Item label={null}>
                            <Row justify="center" gutter={[24, 16]}>
                                <Col span={5}>
                                    <Button type="default" htmlType="submit" onClick={() => {
                                        updateFileNoRequired(true);
                                        fileOperationForm.setFieldsValue({
                                            action: 'query'
                                        });
                                    }}>
                                        查询文件
                                    </Button>
                                </Col>
                                <Col span={5}>
                                    <Button type="primary" htmlType="submit" onClick={() => {
                                        updateFileNoRequired(true);
                                        fileOperationForm.setFieldsValue({
                                            action: 'update'
                                        });
                                    }}>
                                        修改文件
                                    </Button>
                                </Col>
                                <Col span={5}>
                                    <Button type="primary" htmlType="reset">
                                        重置表单
                                    </Button>
                                </Col>
                                <Col span={5}>
                                    <Upload name={"files"}
                                            action={actualUrl}
                                            headers={UserService.getHeader({})}
                                            showUploadList={false} multiple={true}
                                            onChange={(info) => {
                                                if (info.file.status == "done") {
                                                    let response = info.file.response;
                                                    if (response.code == 200) {
                                                        response.main.forEach((item: FileInfo) => {
                                                            refreshFormData(item);
                                                        })
                                                        LogHelper.warn({msg: info.file, className: "ts"})
                                                        message.success(`${info.file.name} 上传成功.`);
                                                    } else {
                                                        message.error(`${info.file.name} 上传失败.`);
                                                        console.log(info.file.response);
                                                    }
                                                }
                                            }}
                                    >
                                        <Button htmlType="submit" icon={<UploadOutlined/>}
                                                onClick={() => {
                                                    updateFileNoRequired(false);

                                                    fileOperationForm.setFieldsValue({
                                                        action: 'upload'
                                                    });

                                                    refreshActualUrl();
                                                }}>上传文件</Button>
                                    </Upload>
                                </Col>
                            </Row>
                        </Form.Item>
                    </Form>
                </Content>
                <HyggeFooter/>
            </Layout>
        </ConfigProvider>
    );

    function refreshFormData(fileInfo: FileInfo) {
        fileOperationForm.setFieldValue("fileNo", fileInfo.fileNo);
        fileOperationForm.setFieldValue("name", fileInfo.name);
        fileOperationForm.setFieldValue("extension", fileInfo.extension);
        fileOperationForm.setFieldValue("fileType", fileInfo.fileType);

        if (fileInfo.description != null) {
            fileOperationForm.setFieldsValue({"description": {"content": fileInfo.description.content}});
            fileOperationForm.setFieldsValue({"description": {"timePointer": dayjs(fileInfo.description.timePointer)}});
        }
    }

    function refreshActualUrl() {
        let cid = fileOperationForm.getFieldValue("cid");
        let fileType = fileOperationForm.getFieldValue("fileType");
        if (PropertiesHelper.isStringNotEmpty(cid)) {
            updateActualUrl(UrlHelper.getBaseApiUrl() + "/main/file?type=" + fileType + "&cid=" + cid);
        } else {
            updateActualUrl(UrlHelper.getBaseApiUrl() + "/main/file?type=" + fileType);
        }
    }

    function refreshCategoryInfoList(updateCategoryInfoList: Function) {
        HomePageService.fetch((data) => {
            let response: AllOverviewInfo | undefined = data?.main
            let categoryContainer: any = [];

            response?.topicOverviewInfoList.forEach((topicOverviewInfo) => {

                let childrenList: any = [];

                topicOverviewInfo.categoryListInfo.forEach((item) => {
                    if (item.categoryType == "DEFAULT") {
                        childrenList.push({
                            title: item.categoryName,
                            value: item.cid,
                        });
                    }
                });

                let parent = {
                    title: topicOverviewInfo.topicInfo.topicName,
                    value: topicOverviewInfo.topicInfo.tid,
                    disabled: true,
                    children: childrenList
                };

                categoryContainer.push(parent)
            });

            updateCategoryInfoList(categoryContainer);

            message.info("类别信息已尝试拉取");
        })
    }

}

const selectItems: DefaultOptionType[] = [
    {
        label: '系统核心图',
        value: 'CORE',
    },
    {
        label: '句子收藏图',
        value: 'QUOTE',
    },
    {
        label: '文章封面',
        value: 'ARTICLE_COVER',
    },
    {
        label: '文章所属',
        value: 'ARTICLE',
    },
    {
        label: '背景音乐',
        value: 'BGM',
    },
    {
        label: '杂项',
        value: 'OTHERS',
    },
];

export default FileOperation;