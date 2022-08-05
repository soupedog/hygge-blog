import * as React from "react"
import {LogHelper} from '../../../utils/UtilContainer';
import {IndexLeft} from "./IndexLeft";
import {IndexRight} from "./IndexRight";
import {Layout} from "antd";

// 描述该组件 props 数据类型
export interface IndexCoreProps {
}

// 描述该组件 states 数据类型
export interface IndexCoreStatus {
}

export class IndexCore extends React.Component<IndexCoreProps, IndexCoreStatus> {
    constructor(props: IndexCoreProps) {
        super(props);
        this.state = {};
        LogHelper.info({className: "IndexCore", msg: "初始化成功"});
    }

    render() {
        return (
            <Layout>
                <IndexLeft/>
                <IndexRight/>
            </Layout>
        );
    }
}
