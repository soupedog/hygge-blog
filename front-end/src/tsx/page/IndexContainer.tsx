import * as React from "react"
import {LogHelper} from '../utils/UtilContainer';
import {IndexCore} from "./component/index/IndexCore";
import zhCN from "antd/lib/locale/zh_CN";
import {ConfigProvider} from "antd";
import {IndexContainerContext} from "./context/HyggeContext";

import '../../css/icon.css';
import 'antd/dist/antd.min.css';
import '../../css/default.css';
import '../../css/index.less';

// 描述该组件 props 数据类型
export interface IndexContainerProps {
}

// 描述该组件 states 数据类型
export interface IndexContainerStatus {
    // 标记当前有多少网络请求
    netWorkArrayCounter?: boolean[]
    // 是否折叠收起
    folded?: boolean;
    updateRootStatus?: Function;
}

export class IndexContainer extends React.Component<IndexContainerProps, IndexContainerStatus> {
    constructor(props: IndexContainerProps) {
        super(props);
        this.state = {
            netWorkArrayCounter: [],
            folded: true,
            updateRootStatus: this.updateRootStatus.bind(this)
        };
        LogHelper.info({className: "IndexContainer", msg: "初始化成功"});
    }

    render() {
        return (
            <ConfigProvider locale={zhCN}>
                <IndexContainerContext.Provider value={this.state}>
                    <IndexCore/>
                </IndexContainerContext.Provider>
            </ConfigProvider>
        );
    }

    updateRootStatus(deltaInfo: IndexContainerStatus) {
        this.setState(deltaInfo);
    }
}