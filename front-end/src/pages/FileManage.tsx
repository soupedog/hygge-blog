import {useEffect, useState} from 'react';
import {type FileInfo} from '../util/ApiClient.ts';
import {Button, Card, Col, Flex, type GetProp, Image, Layout, message, Modal, Row, Space, Switch, Table, type TableProps} from 'antd';
import {createStyles} from 'antd-style';
import type {SorterResult} from 'antd/es/table/interface';
import AppBaseHeader from './component/AppBaseHeader.tsx';
import {useIsMutating} from '@tanstack/react-query';
import {Content} from 'antd/es/layout/layout';
import Column from 'antd/es/table/Column';
import AppFooter from './component/AppFooter.tsx';

import imageNotFound from '../assets/imageNotFound.png'
import imageDefault from '../assets/imageDefault.png'
import {useFileCService} from '../util/ApiService.ts';
import Search from 'antd/es/input/Search';

const useStyle = createStyles(({css, token}) => {
    // @ts-ignore
    const {antCls} = token;
    return {
        customTable: css`
            ${antCls}-table {
                ${antCls}-table-container {
                    ${antCls}-table-body,
                    ${antCls}-table-content {
                        scrollbar-width: thin;
                        scrollbar-color: #eaeaea transparent;
                        scrollbar-gutter: stable;
                    }
                }
            }
        `,
    };
});

type TablePaginationConfig = Exclude<GetProp<TableProps, 'pagination'>, boolean>;

interface TableParams {
    pagination?: TablePaginationConfig;
    sortField?: SorterResult<any>['field'];
    sortOrder?: SorterResult<any>['order'];
    filters?: Parameters<GetProp<TableProps, 'onChange'>>[1];
}

export default function FileManage() {
    const {styles} = useStyle();
    const {fetchFileInfo, downloadFilePromise, deleteFile} = useFileCService();
    const isAnyPending = useIsMutating() > 0;

    const [deleteModalOpen, setDeleteModalOpen] = useState(false);
    const [currentFileInfo, setCurrentFileInfo] = useState<FileInfo | undefined>(undefined);
    const [currentAction, setCurrentAction] = useState<'preview' | 'delete'>('preview');
    const [keywords, setKeywords] = useState('');
    const [data, setData] = useState<FileInfo[]>([]);
    const [tableParams, setTableParams] = useState<TableParams>({
        pagination: {
            current: 1,
            pageSize: 10,
        },
    });

    const fetchData = () => {
        // @ts-ignore
        let types: Array<'CORE' | 'QUOTE' | 'ARTICLE_COVER' | 'ARTICLE' | 'BGM' | 'OTHERS'> = tableParams?.filters?.fileType;

        fetchFileInfo.mutate({
                types: types,
                keywords: keywords,
                currentPage: tableParams.pagination!.current!,
                pageSize: tableParams.pagination!.pageSize!
            },
            {
                onSuccess: (data) => {
                    setData(data.fileInfoList);
                    setTableParams({
                        ...tableParams,
                        pagination: {
                            ...tableParams.pagination,
                            showSizeChanger: true,
                            total: data.totalCount,
                        },
                    });
                }
            });
    };

    const assignBlobImageToElement = (fileNo: string,) => {
        downloadFilePromise.mutate(fileNo, {
            onSuccess: axiosResponse => {
                const firstImg: HTMLImageElement | null = document.querySelector('#img_' + fileNo + ' img:first-child');
                const secondImg: HTMLImageElement | null = document.querySelector('.ant-image-preview-img');

                const elements: HTMLImageElement[] = [];
                if (firstImg) {
                    elements.push(firstImg);
                }
                if (secondImg) {
                    elements.push(secondImg);
                }

                if (elements.length < 1) {
                    message.warning('未找到对应图片展示标签！');
                    return;
                }

                // @ts-ignore
                const type: string = axiosResponse.headers['content-type'];
                const blob = new Blob([axiosResponse.data], {type: type});
                const url = URL.createObjectURL(blob);

                elements.forEach((element) => {
                    element.src = url;
                    element.onload = function () {
                        URL.revokeObjectURL(url);
                    };
                });
                message.success('图片加载成功。');
            }
        });
    };

    const removeFile = (fileNo: string) => {
        deleteFile.mutate(fileNo, {
            onSuccess: data => {
                message.success('文件删除成功。');

                // 删除完成后重新加载数据
                fetchData();
            }
        });
    };

    const handleTableChange: TableProps<FileInfo>['onChange'] = (pagination, filters, sorter) => {
        setTableParams({
            pagination,
            filters,
            sortOrder: Array.isArray(sorter) ? undefined : sorter.order,
            sortField: Array.isArray(sorter) ? undefined : sorter.field,
        });

        // `dataSource` is useless since `pageSize` changed
        if (pagination.pageSize !== tableParams.pagination?.pageSize) {
            setData([]);
        }
    };

    useEffect(() => {
        // 依赖静态值表示仅初始化时调用一次
        fetchData();
    }, []);

    useEffect(fetchData, [
        tableParams.pagination?.current,
        tableParams.pagination?.pageSize,
        tableParams?.sortOrder,
        tableParams?.sortField,
        JSON.stringify(tableParams.filters),
    ]);

    return (
        <Layout className={'full-screen-min-y'}>
            <AppBaseHeader title={'文件管理'} isAnyPending={isAnyPending}/>
            <Modal key={'deleteFileModal'}
                   title='请注意'
                   open={deleteModalOpen}
                   onOk={(event) => {
                       removeFile(currentFileInfo?.fileNo ?? '');
                       setDeleteModalOpen(false);
                   }}
                   okButtonProps={{danger: true}}
                   confirmLoading={isAnyPending}
                   onCancel={(event) => {
                       setDeleteModalOpen(false);
                   }}
            >
                <p>删除操作不可逆，请再次确认是否要删除 【{currentFileInfo?.name}】 ？</p>
            </Modal>
            <Content style={{padding: '0 50px'}}>
                <Card variant='borderless'>
                    <Row>
                        <Flex justify={'flex-end'} style={{width: '100%'}}>
                            <Col span={6}>
                                <Search
                                    placeholder='关键字过滤'
                                    enterButton='查询'
                                    size='large'
                                    onChange={event => {
                                        setKeywords(event.target.value);
                                    }}
                                    onSearch={(value) => {
                                        if (tableParams.pagination!.current == 1) {
                                            fetchData();
                                        } else {
                                            setTableParams({
                                                ...tableParams,
                                                pagination: {
                                                    ...tableParams.pagination,
                                                    current: 1,
                                                    showSizeChanger: true,
                                                },
                                            });
                                        }
                                    }}
                                />
                            </Col>
                        </Flex>
                    </Row>
                    <Table<FileInfo>
                        className={styles.customTable}
                        rowKey={(record) => record.fileNo}
                        dataSource={data}
                        expandable={{
                            expandedRowRender: (record) => <p
                                style={{margin: 0}}>{record.description?.content}</p>,
                            rowExpandable: (record) => record.description?.content != undefined,
                        }}
                        scroll={{x: 'max-content'}}
                        pagination={tableParams.pagination}
                        loading={isAnyPending}
                        onChange={handleTableChange}
                    >
                        <Column title='缩略图' dataIndex='fileNo' fixed={'left'}
                                render={(value: any, record: FileInfo) => (
                                    record.extension != 'png' && record.extension != 'jpg' && record.extension != 'jpeg' && record.extension != 'gif' ?
                                        <Image id={'img_' + record.fileNo}
                                               width={40}
                                               preview={false}
                                               fallback={imageNotFound}
                                        /> :
                                        <Image id={'img_' + record.fileNo}
                                               width={40}
                                               fallback={imageDefault}
                                               classNames={{root: 'img_p_' + record.fileNo}}
                                               preview={{
                                                   open: currentAction == 'preview' && record.fileNo == currentFileInfo?.fileNo,
                                                   onOpenChange: (value) => {
                                                       if (value) {
                                                           setCurrentFileInfo(record);
                                                       } else {
                                                           setCurrentFileInfo(undefined);
                                                       }
                                                   },
                                               }}
                                        />
                                )}
                        />
                        <Column title='文件名' dataIndex='name' fixed={'left'}/>
                        <Column title='归档类型' dataIndex='fileType' fixed={'left'}
                                filters={[
                                    {text: '系统核心图', value: 'CORE'},
                                    {text: '句子收藏图', value: 'QUOTE'},
                                    {text: '文章封面', value: 'ARTICLE_COVER'},
                                    {text: '文章所属', value: 'ARTICLE'},
                                    {text: '背景音乐', value: 'BGM'},
                                    {text: '杂项', value: 'OTHERS'},
                                ]}/>
                        <Column title='扩展名' dataIndex='extension' fixed={'left'}/>
                        <Column title='大小' dataIndex='fileSize'/>
                        <Column title='磁盘副本' dataIndex='isInHardDisk'
                                render={(_: any, record: FileInfo) => (
                                    record.cacheLink == undefined ? <Switch disabled/> :
                                        <Switch disabled defaultChecked/>
                                )}
                        />
                        <Column title='路径' dataIndex='src'
                                render={(_: any, record: FileInfo) => (
                                    record.cacheLink == undefined ?
                                        <Button color='default' variant='link' onClick={() => {
                                            navigator.clipboard.writeText(record.relativePath).then(() => {
                                                message.info('已成功复制 ' + record.name + ' 相对路径到剪切板！')
                                            }).catch(e => console.error(e))
                                        }}>
                                            {record.relativePath}
                                        </Button> :
                                        <Button color='primary' variant='link' onClick={() => {
                                            let link: string = record.cacheLink!;
                                            navigator.clipboard.writeText(link).then(() => {
                                                message.info('已成功复制 ' + record.name + ' 静态链接到剪切板！')
                                            }).catch(e => console.error(e))
                                        }}>
                                            {record.cacheLink}
                                        </Button>
                                )}
                        />
                        <Column title='编号' dataIndex='fileNo'/>
                        <Column
                            title='操作'
                            key='action'
                            fixed={'right'}
                            render={(_: any, record: FileInfo) => (
                                <Space size='small'>
                                    <Button color='cyan' variant='filled' onClick={() => {
                                        //TODO 需要进行网络请求生成一个一次性 fileKey
                                        let link: string = record.apiLink;
                                        navigator.clipboard.writeText(link).then(() => {
                                            message.info('已成功复制 ' + record.name + ' 动态链接到剪切板！')
                                        }).catch(e => console.error(e))
                                    }}>
                                        复制链接
                                    </Button>
                                    <Button
                                        disabled={record.extension != 'png' && record.extension != 'jpg' && record.extension != 'jpeg' && record.extension != 'gif'}
                                        color='default' variant='outlined' onClick={() => {
                                        setCurrentAction('preview');
                                        setCurrentFileInfo(record);
                                        assignBlobImageToElement(record.fileNo);
                                    }}>
                                        查看
                                    </Button>
                                    <Button color='cyan' variant='solid' onClick={() => {
                                        // let filePromise = FileService.downloadFilePromise(record.fileNo)
                                        // let fileName = record.name + '.' + record.extension;
                                        // FileService.saveFile(filePromise, fileName);
                                    }}>
                                        下载
                                    </Button>
                                    <Button color='primary' variant='solid' onClick={() => {
                                        // navigate('/file/operation/' + record.fileNo);
                                    }}>
                                        编辑
                                    </Button>
                                    <Button color='danger' variant='solid' onClick={async () => {
                                        setCurrentAction('delete');
                                        setCurrentFileInfo(record);
                                        setDeleteModalOpen(true);
                                    }}>
                                        删除
                                    </Button>
                                </Space>
                            )}
                        />
                    </Table>
                </Card>
            </Content>
            <AppFooter/>
        </Layout>
    );
}
