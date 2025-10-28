import React, {useEffect, useState} from 'react';
import type {GetProp, TableProps} from 'antd';
import {ConfigProvider, Layout, Table} from "antd";

import zhCN from "antd/lib/locale/zh_CN";
import {Content, Header} from "antd/es/layout/layout";
import {class_index_title,} from "../component/properties/ElementNameContainer";
import HyggeFooter from "../component/HyggeFooter";
import type {SorterResult} from 'antd/es/table/interface';
import {LogHelper} from "../util/UtilContainer";
import {FileInfo, FileService} from "../rest/ApiClient";

type ColumnsType<T extends object = object> = TableProps<T>['columns'];
type TablePaginationConfig = Exclude<GetProp<TableProps, 'pagination'>, boolean>;

interface TableParams {
    pagination?: TablePaginationConfig;
    sortField?: SorterResult<any>['field'];
    sortOrder?: SorterResult<any>['order'];
    filters?: Parameters<GetProp<TableProps, 'onChange'>>[1];
}

const columns: ColumnsType<FileInfo> = [
    {
        title: '文件名',
        dataIndex: 'name',
        width: '20%',
    },
    {
        title: '扩展名',
        dataIndex: 'extension',
    },
    {
        title: '文件大小',
        dataIndex: 'fileSize',
    },
    {
        title: '归档类型',
        dataIndex: 'fileType',
        filters: [
            {text: '系统核心图', value: 'CORE'},
            {text: '句子收藏图', value: 'QUOTE'},
            {text: '文章封面', value: 'ARTICLE_COVER'},
            {text: '文章所属', value: 'ARTICLE'},
            {text: '背景音乐', value: 'BGM'},
            {text: '杂项', value: 'OTHERS'},
        ],
    },
    {
        title: '相对路径',
        dataIndex: 'src',
    },
    {
        title: '编号',
        dataIndex: 'fileNo',
    },
];

function FileManage() {
    const [data, setData] = useState<FileInfo[]>();
    const [loading, setLoading] = useState(false);
    const [tableParams, setTableParams] = useState<TableParams>({
        pagination: {
            current: 1,
            pageSize: 10,
        },
    });

    LogHelper.warn({className: "FileManage", msg: tableParams, isJson: true})

    const fetchData = () => {
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
            <Layout className="layout">
                <Header>
                    <div className={class_index_title + " floatToLeft"} style={{width: 200}}>我的小宅子---文件管理</div>
                </Header>
                <Content style={{padding: '0 50px', minHeight: window.innerHeight - 226}}>
                    <Table<FileInfo>
                        columns={columns}
                        rowKey={(record) => record.fileNo}
                        dataSource={data}
                        pagination={tableParams.pagination}
                        loading={loading}
                        onChange={handleTableChange}
                    />
                </Content>
                <HyggeFooter/>
            </Layout>
        </ConfigProvider>
    );
}

export default FileManage;