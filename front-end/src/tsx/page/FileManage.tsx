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
import {UrlHelper} from "../util/UtilContainer";
import {FileInfo, FileService} from "../rest/ApiClient";
import Column from "antd/es/table/Column";
import {createStyles} from 'antd-style';

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
        FileService.findFileInfo(types, (data) => {
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
                                                       fallback={UrlHelper.getBaseStaticSourceUrl() + "/core/imageNotFound.png"}
                                                /> :
                                                <Image id={"img_" + record.fileNo}
                                                       width={40}
                                                       fallback={UrlHelper.getBaseStaticSourceUrl() + "/core/imageDefault.png"}
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
                                            record.isInHardDisk == undefined ? <Switch disabled/> :
                                                <Switch disabled defaultChecked/>
                                        )}
                                />
                                <Column title="路径" dataIndex="src"
                                        render={(_: any, record: FileInfo) => (
                                            record.isInHardDisk == undefined ?
                                                <Button color="default" variant="link" onClick={() => {
                                                    navigator.clipboard.writeText(record.src).then(() => {
                                                        message.info("已成功复制 " + record.name + " 相对路径到剪切板！")
                                                    }).catch(e => console.error(e))
                                                }}>
                                                    {record.src}
                                                </Button> :
                                                <Button color="primary" variant="link" onClick={() => {
                                                    let link: string = UrlHelper.getBaseStaticSourceUrl() + record.src;
                                                    navigator.clipboard.writeText(link).then(() => {
                                                        message.info("已成功复制 " + record.name + " 静态链接到剪切板！")
                                                    }).catch(e => console.error(e))
                                                }}>
                                                    {record.src}
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
                                                let link: string = UrlHelper.getBaseApiUrl() + "/main/file/static/" + record.fileNo;
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
                                                alert(record.fileNo)
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