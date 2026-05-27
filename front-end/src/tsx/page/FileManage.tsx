import React, {createContext, useEffect, useState} from 'react';
import {
    Button,
    Card,
    ConfigProvider,
    GetProp,
    Image,
    Layout,
    message,
    Modal,
    Space,
    Switch,
    Table,
    TableProps
} from 'antd';

import zhCN from "antd/lib/locale/zh_CN";
import {Content, Header} from "antd/es/layout/layout";
import {class_index_title,} from "../component/properties/ElementNameContainer";
import HyggeFooter from "../component/HyggeFooter";
import type {SorterResult} from 'antd/es/table/interface';
import {FileInfo, FileService} from "../rest/ApiClient";
import Column from "antd/es/table/Column";
import {createStyles} from 'antd-style';
import {useNavigate} from "react-router-dom";

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

const ReachableContext = createContext<string | null>(null);

const config = {
    title: '操作提醒',
    content: (
        <>
            <ReachableContext.Consumer>{(name) => `删除操作不可逆，是否确定要删除：${name}？`}</ReachableContext.Consumer>
        </>
    ),
};

function FileManage() {
    const navigate = useNavigate();
    const [data, setData] = useState<FileInfo[]>();
    const [currentFileName, setCurrentFileName] = useState<string>("未选择");
    const [loading, setLoading] = useState(false);
    const [tableParams, setTableParams] = useState<TableParams>({
        pagination: {
            current: 1,
            pageSize: 10,
        },
    });

    const {styles} = useStyle();

    const [modal, contextHolder] = Modal.useModal();

    const fetchData = () => {
        // 改页面标题
        document.title = "管理文件";
        setLoading(true);
        // @ts-ignore
        let types: [] = tableParams?.filters?.fileType;
        FileService.findFileInfoMulti(types, (data) => {
            setData(data == null ? [] : data.main?.fileInfoList);
            setLoading(false);
            setTableParams({
                ...tableParams,
                pagination: {
                    ...tableParams.pagination,
                    showSizeChanger: true,
                    total: data == null ? 0 : data.main?.totalCount,
                },
            });
        });
    };

    useEffect(fetchData, [
        tableParams.pagination?.current,
        tableParams.pagination?.pageSize,
        tableParams?.sortOrder,
        tableParams?.sortField,
        JSON.stringify(tableParams.filters),
    ]);

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

    return (
        <ConfigProvider locale={zhCN}>
            <ReachableContext.Provider value={currentFileName}>
                <Layout className="layout">
                    <Header>
                        <div className={class_index_title + " floatToLeft"} style={{width: 200}}>我的小宅子---文件管理
                        </div>
                    </Header>
                    <Content style={{padding: '0 50px', minHeight: window.innerHeight - 226}}>
                        <Card variant="borderless">
                            <Table<FileInfo>
                                className={styles.customTable}
                                // columns={columns}
                                rowKey={(record) => record.fileNo}
                                dataSource={data}
                                expandable={{
                                    expandedRowRender: (record) => <p
                                        style={{margin: 0}}>{record.description?.content}</p>,
                                    rowExpandable: (record) => record.description?.content != undefined,
                                }}
                                scroll={{x: 'max-content'}}
                                pagination={tableParams.pagination}
                                loading={loading}
                                onChange={handleTableChange}
                            >
                                <Column title="缩略图" dataIndex="fileNo" fixed={"left"}
                                        render={(_: any, record: FileInfo) => (
                                            record.extension != "png" && record.extension != "jpg" && record.extension != "jpeg" && record.extension != "gif" ?
                                                <Image id={"img_" + record.fileNo}
                                                       width={40}
                                                       preview={false}
                                                       fallback={"data:image/png;base64,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"}
                                                /> :
                                                <Image id={"img_" + record.fileNo}
                                                       width={40}
                                                       fallback={"data:image/png;base64,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"}
                                                       preview={{
                                                           forceRender: true,
                                                           rootClassName: "img_p_" + record.fileNo
                                                       }}
                                                />
                                        )}
                                />
                                <Column title="文件名" dataIndex="name" fixed={"left"}/>
                                <Column title="归档类型" dataIndex="fileType" fixed={"left"}
                                        filters={[
                                            {text: "系统核心图", value: "CORE"},
                                            {text: '句子收藏图', value: 'QUOTE'},
                                            {text: '文章封面', value: 'ARTICLE_COVER'},
                                            {text: '文章所属', value: 'ARTICLE'},
                                            {text: '背景音乐', value: 'BGM'},
                                            {text: '杂项', value: 'OTHERS'},
                                        ]}/>
                                <Column title="扩展名" dataIndex="extension" fixed={"left"}/>
                                <Column title="大小" dataIndex="fileSize"/>
                                <Column title="磁盘副本" dataIndex="isInHardDisk"
                                        render={(_: any, record: FileInfo) => (
                                            record.cacheLink == undefined ? <Switch disabled/> :
                                                <Switch disabled defaultChecked/>
                                        )}
                                />
                                <Column title="路径" dataIndex="src"
                                        render={(_: any, record: FileInfo) => (
                                            record.cacheLink == undefined ?
                                                <Button color="default" variant="link" onClick={() => {
                                                    navigator.clipboard.writeText(record.relativePath).then(() => {
                                                        message.info("已成功复制 " + record.name + " 相对路径到剪切板！")
                                                    }).catch(e => console.error(e))
                                                }}>
                                                    {record.relativePath}
                                                </Button> :
                                                <Button color="primary" variant="link" onClick={() => {
                                                    let link: string = record.cacheLink!;
                                                    navigator.clipboard.writeText(link).then(() => {
                                                        message.info("已成功复制 " + record.name + " 静态链接到剪切板！")
                                                    }).catch(e => console.error(e))
                                                }}>
                                                    {record.cacheLink}
                                                </Button>
                                        )}
                                />
                                <Column title="编号" dataIndex="fileNo"/>
                                <Column
                                    title="操作"
                                    key="action"
                                    fixed={"right"}
                                    render={(_: any, record: FileInfo) => (
                                        <Space size="small">
                                            <Button color="cyan" variant="filled" onClick={() => {
                                                //TODO 需要进行网络请求生成一个一次性 fileKey
                                                let link: string = record.apiLink;
                                                navigator.clipboard.writeText(link).then(() => {
                                                    message.info("已成功复制 " + record.name + " 动态链接到剪切板！")
                                                }).catch(e => console.error(e))
                                            }}>
                                                复制链接
                                            </Button>
                                            <Button
                                                disabled={record.extension != "png" && record.extension != "jpg" && record.extension != "jpeg" && record.extension != "gif"}
                                                color="default" variant="outlined" onClick={() => {
                                                let firstImg: HTMLImageElement | null = document.querySelector('#img_' + record.fileNo + ' img:first-child');
                                                const parentElement = document.getElementsByClassName('img_p_' + record.fileNo)[0];
                                                let secondImg: HTMLImageElement | null = parentElement?.querySelector('img');
                                                let filePromise = FileService.downloadFilePromise(record.fileNo)
                                                // @ts-ignore
                                                FileService.assignBlobImageToElement(filePromise, firstImg, secondImg);
                                            }}>
                                                查看
                                            </Button>
                                            <Button color="cyan" variant="solid" onClick={() => {
                                                let filePromise = FileService.downloadFilePromise(record.fileNo)
                                                let fileName = record.name + "." + record.extension;
                                                FileService.saveFile(filePromise, fileName);
                                            }}>
                                                下载
                                            </Button>
                                            <Button color="primary" variant="solid" onClick={() => {
                                                navigate("/file/operation/" + record.fileNo);
                                            }}>
                                                编辑
                                            </Button>
                                            <Button color="danger" variant="solid" onClick={async () => {
                                                setCurrentFileName(record.name);
                                                const confirmed = await modal.confirm(config);
                                                if (confirmed) {
                                                    FileService.deleteFile(record.fileNo, (data) => {
                                                        message.info("删除 " + record.name + " 成功！");
                                                        // 主动触发数据重新加载
                                                        fetchData();
                                                    });
                                                }
                                            }}>
                                                删除
                                            </Button>
                                        </Space>
                                    )}
                                />
                            </Table>
                        </Card>
                    </Content>
                    <HyggeFooter/>
                </Layout>
                {/* `contextHolder` should always be placed under the context you want to access */}
                {contextHolder}
            </ReachableContext.Provider>
        </ConfigProvider>
    );
}

export default FileManage;