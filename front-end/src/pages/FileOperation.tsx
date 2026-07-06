import {useEffect, useState} from 'react';
import AppBaseHeader from './component/AppBaseHeader.tsx';
import {BorderBeam, Button, Card, Col, DatePicker, Empty, Flex, Form, Image, Input, Layout, message, Modal, Radio, Row, Select, Space, Typography, Upload} from 'antd';
import {useIsMutating} from '@tanstack/react-query';
import AppFooter from './component/AppFooter.tsx';
import {Content} from 'antd/es/layout/layout';
import {type FileInfo, type FileInfoAddUpdateInput, UserClient} from '../util/ApiClient.ts';
import {useSearchParams} from 'react-router-dom';
import PropertiesHelper from '../util/PropertiesHelper.ts';
import {UploadOutlined} from '@ant-design/icons';
import UrlHelper from '../util/UrlHelper.ts';
import type {DefaultOptionType} from 'antd/es/select/index';
import {useFileService, useHomeService} from '../util/ApiService.ts';
import imageNotFound from '../assets/imageNotFound.png'
import dayjs from 'dayjs';
import customParseFormat from 'dayjs/plugin/customParseFormat';
import Title from 'antd/es/typography/Title';

dayjs.extend(customParseFormat);

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

export default function FileOperation() {
    const isAnyPending = useIsMutating() > 0;

    const {fetchPermission} = useHomeService();

    const {getFileInfoByFileNo, updateFile} = useFileService();

    const [searchParams, setSearchParams] = useSearchParams();
    const [fileNo, setFileNo] = useState(searchParams.get('fileNo') || undefined);
    const [currentFileType, setCurrentFileType] = useState('');
    const [currentPermissionId, setCurrentPermissionId] = useState('');

    const [fileInfo, setFileInfo] = useState<FileInfo | undefined>(undefined);

    const [permissionOptions, setPermissionOptions] = useState([]);

    const [queryModalOpen, setQueryModalOpen] = useState(false);
    const [fileForm] = Form.useForm<FileInfoAddUpdateInput>();

    const [formMode, setFormMode] = useState<'query' | 'add' | 'update'>(fileNo ? 'query' : 'add');

    const isAddMode = formMode == 'add';
    const isQueryMode = formMode == 'query';
    const isUpdateMode = formMode == 'update';

    const isImage = fileInfo != null && (
        fileInfo.extension == 'jpg'
        || fileInfo.extension == 'jpeg'
        || fileInfo.extension == 'gif'
        || fileInfo.extension == 'png'
    );

    const onFileNoChange = (nextFileNo?: string) => {
        // 复制一个新的 URLSearchParams 对象
        const nextParams = new URLSearchParams(searchParams);
        if (PropertiesHelper.isStringNotEmpty(nextFileNo)) {
            setFileNo(nextFileNo);
            nextParams.set('fileNo', nextFileNo);
        } else {
            setFileNo(undefined);
            nextParams.delete('fileNo');
        }
        setSearchParams(nextParams);
    };

    const freshFileInfo = () => {
        if (fileNo) {
            getFileInfoByFileNo.mutate({fileNo: fileNo, accessCountMin: 1}, {
                onSuccess: data => {
                    setFileInfo(data);
                    message.info('文件信息拉取成功！');
                }
            });
        }
    };

    const modifyFileInfo = (input: FileInfoAddUpdateInput) => {
        updateFile.mutate(input, {
                onSuccess: data => {
                    message.success({content: '修改文件信息成功！'});
                }
            }
        );
    }

    useEffect(() => {
        // 依赖静态值表示仅初始化时调用一次
        document.title = `文件操作 | 我的小宅子`;

        fetchPermission.mutate(undefined, {
            onSuccess: data => {
                const nextPermissionOptions: any[] = [];

                data.map(item => {
                    nextPermissionOptions.push(
                        {
                            label: item.name,
                            value: item.permissionId,
                        }
                    );
                });

                // @ts-ignore
                setPermissionOptions(nextPermissionOptions);
                message.info({content: '授权信息拉取成功！'});
            },
        });

        freshFileInfo();
    }, []);

    useEffect(() => {
        if (fileInfo) {
            fileForm.setFieldsValue({
                fileNo: fileInfo.fileNo,
                permissionId: fileInfo.permissionId,
                name: fileInfo.name,
                extension: fileInfo.extension,
                fileCacheType: fileInfo.fileCacheType,
                fileType: fileInfo.fileType,
                description: {
                    timePointer: fileInfo.description?.timePointer,
                    content: fileInfo.description?.content,
                    nginxLink: fileInfo.description?.nginxLink
                }
            });
        }
    }, [JSON.stringify(fileInfo)]);

    return (
        <Layout className={'full-screen-min-y'}>
            <AppBaseHeader title={'文件操作'} isAnyPending={isAnyPending}/>
            <Modal key={'queryFileModal'}
                   title='请注意'
                   open={queryModalOpen}
                   onOk={(event) => {
                       freshFileInfo();
                       setQueryModalOpen(false);
                   }}
                   confirmLoading={isAnyPending}
                   onCancel={(event) => {
                       setQueryModalOpen(false);
                   }}
            >
                <p>执行查询将丢失正在编辑的数据！</p>
            </Modal>
            <Content>
                <Form
                    // 不再记录历史信息
                    autoComplete={'off'}
                    form={fileForm}
                    name='hygge_file_operation'
                    style={{padding: '4rem'}}
                    onFinish={(value) => {
                        if (value.action == 'update') {
                            // @ts-ignore
                            value.name = PropertiesHelper.stringOfNullable({target: value.name, defaultValue: null});
                            // @ts-ignore
                            value.description.content = PropertiesHelper.stringOfNullable({target: value.description.content, defaultValue: null});
                            // @ts-ignore
                            value.description.nginxLink = PropertiesHelper.stringOfNullable({target: value.description.nginxLink, defaultValue: null});

                            modifyFileInfo(value);
                        }
                    }}
                >
                    <Row gutter={'4rem'}>
                        <Col offset={1} span={22}>
                            <BorderBeam>
                                <Typography>
                                    <Title level={3} style={{color: '#0c13d1'}}>预览效果：</Title>
                                </Typography>
                                <Card className={'align-center'} style={{marginBottom: '2rem'}}>
                                    <Flex justify={'center'}>
                                        {fileInfo ?
                                            isImage ? <Image width={'16%'} height={'9%'} src={fileInfo.apiLink}/> : <Image width={'16%'} height={'9%'} src={imageNotFound}/>
                                            : <Empty/>}
                                    </Flex>
                                </Card>
                            </BorderBeam>
                        </Col>
                    </Row>
                    <Row gutter={'4rem'}>
                        <Col offset={1} span={8}>
                            <Form.Item name={['fileNo']} label='文件编号'
                                       rules={[{required: !isAddMode}]}>
                                <Input value={fileNo} onChange={(event) => {
                                    onFileNoChange(event.target.value);
                                }}/>
                            </Form.Item>
                        </Col>
                        <Col span={8}>
                            <Form.Item name={['fileCacheType']} label='副本备份类型'
                                       rules={[{required: true}]}
                                       initialValue={'DEFAULT'}>
                                <Radio.Group>
                                    <Radio value={'DEFAULT'}>无副本</Radio>
                                    <Radio value={'NGINX'}>Nginx 备份</Radio>
                                </Radio.Group>
                            </Form.Item>
                        </Col>
                        <Col span={7}>
                            <Form.Item name={['fileType']} label='文件类型' rules={[{required: isAddMode || isUpdateMode}]}>
                                <Select
                                    value={currentFileType}
                                    onChange={value => setCurrentFileType(value)}
                                    options={selectItems}
                                    placeholder='请选择文件类型'
                                />
                            </Form.Item>
                        </Col>
                    </Row>
                    <Row gutter={'4rem'}>
                        <Col offset={1} span={10}>
                            <Form.Item name={['name']} label='文件名称' rules={[{required: false}]}>
                                <Input/>
                            </Form.Item>
                        </Col>
                        <Col span={5}>
                            <Form.Item name={['extension']} label='扩展名' rules={[{required: false}]}>
                                <Input/>
                            </Form.Item>
                        </Col>
                        <Col span={8}>
                            <Form.Item name={['permissionId']} label='授权类型' rules={[{required: isAddMode || isUpdateMode}]}>
                                <Select
                                    style={{width: '100%'}}
                                    styles={{
                                        popup: {root: {maxHeight: 400, overflow: 'auto'}},
                                    }}
                                    options={permissionOptions}
                                    value={currentPermissionId}
                                    onChange={value => {
                                        setCurrentPermissionId(value);
                                    }}
                                    placeholder='请选择授权类型'
                                />
                            </Form.Item>
                        </Col>
                    </Row>
                    <Row gutter={'4rem'}>
                        <Col offset={1} span={12}>
                            <Form.Item name={['description', 'timePointer']} label='图片发生时间'
                                       getValueProps={(value) => ({
                                           // 这里的 value 是从表单数据中读取的字符串
                                           value: value ? dayjs(value) : null,
                                       })}
                                       rules={[{required: false}]}>
                                <DatePicker showTime format={'YYYY-MM-DD HH:mm:ss'}/>
                            </Form.Item>
                        </Col>
                    </Row>
                    <Row gutter={'4rem'}>
                        <Col offset={1} span={22}>
                            <Form.Item name={['description', 'content']} label='文件备注' rules={[{required: false}]}>
                                <Input.TextArea rows={2}/>
                            </Form.Item>
                        </Col>
                    </Row>
                    <Row gutter={'4rem'}>
                        <Form.Item className={'display-none'} name={['action']} label='操作类型'
                                   rules={[{required: true}]}
                                   initialValue={'query'}>
                            <Radio.Group>
                                <Radio value={'query'}>查询文件</Radio>
                                <Radio value={'update'}>修改文件</Radio>
                                <Radio value={'upload'}>上传文件</Radio>
                            </Radio.Group>
                        </Form.Item>
                        <Col offset={1} span={22}>
                            <Form.Item>
                                <Flex justify={'center'} style={{alignItems: 'center'}}>
                                    <Space size={'large'} align={'end'}>
                                        <Button type='dashed' htmlType='reset' onClick={() => {
                                            setFormMode('query');
                                            setFileInfo(undefined);
                                        }}>
                                            重置表单
                                        </Button>
                                        <Upload name={'files'}
                                                maxCount={1}
                                                action={`${UrlHelper.getApiPrefix()}/main/file?type=${currentFileType}&permissionId=${currentPermissionId}`}
                                                headers={UserClient.getHeader({})}
                                                showUploadList={false} multiple={false}
                                                onChange={(info) => {
                                                    if (info.file.status == 'done') {
                                                        let response = info.file.response;
                                                        if (response.code == 200) {
                                                            response.main.forEach((item: FileInfo) => {
                                                                setFileInfo(item);
                                                            });
                                                            message.success(`${info.file.name} 上传成功.`);
                                                        } else {
                                                            message.error(`${info.file.name} 上传失败.`);
                                                            console.log(info.file.response);
                                                        }
                                                    }
                                                }}
                                        >
                                            <Button type='primary' htmlType='submit' icon={<UploadOutlined/>}
                                                    onClick={() => {
                                                        fileForm.setFieldsValue({
                                                            action: 'add'
                                                        });
                                                        setFormMode('add');
                                                    }}>上传文件</Button>
                                        </Upload>
                                        <Button type='primary' htmlType='button' danger onClick={() => {
                                            fileForm.setFieldsValue({
                                                action: 'update'
                                            });
                                            setFormMode('update');
                                            fileForm.submit();
                                        }}>
                                            修改文件信息
                                        </Button>
                                        <Button type='dashed' color={'purple'} htmlType='submit' onClick={() => {
                                            setFormMode('query');
                                            if (PropertiesHelper.isStringNotEmpty(fileNo)) {
                                                fileForm.setFieldsValue({
                                                    action: 'query'
                                                });
                                                setQueryModalOpen(true);
                                            }
                                        }}>
                                            查询文件信息
                                        </Button>
                                    </Space>
                                </Flex>
                            </Form.Item>
                        </Col>
                    </Row>
                </Form>
            </Content>
            <AppFooter/>
        </Layout>
    );
}
