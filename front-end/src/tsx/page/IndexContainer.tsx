import * as React from "react"
import {LogHelper} from '../utils/UtilContainer';
import zhCN from "antd/lib/locale/zh_CN";
import {ConfigProvider, Layout} from "antd";
import {IndexContainerContext} from "./context/HyggeContext";

import '../../css/icon.css';
import 'antd/dist/antd.min.css';
import '../../css/default.css';
import '../../css/index.less';
import {UserResponse, UserService} from "../rest/ApiClient";
import {IndexLeft} from "./component/index/IndexLeft";
import {IndexRight} from "./component/index/IndexRight";

// 描述该组件 props 数据类型
export interface IndexContainerProps {
}

// 描述该组件 states 数据类型
export interface IndexContainerState {
    currentUser?: UserResponse | null;
    // 标记当前有多少网络请求
    netWorkArrayCounter?: boolean[]
    // 是否折叠收起
    folded?: boolean;
    searchType?: SearchType;
    updateRootStatus?: Function;
}

export enum SearchType {
    ARTICLE,
    QUOTE
}

export class IndexContainer extends React.Component<IndexContainerProps, IndexContainerState> {
    constructor(props: IndexContainerProps) {
        super(props);
        this.state = {
            currentUser: UserService.getCurrentUser(),
            netWorkArrayCounter: [],
            folded: true,
            searchType: SearchType.ARTICLE,
            updateRootStatus: this.updateRootStatus.bind(this)
        };
        LogHelper.info({className: "IndexContainer", msg: "初始化成功"});
    }

    render() {
        return (
            <ConfigProvider locale={zhCN}>
                <IndexContainerContext.Provider value={this.state}>
                    <Layout>
                        <IndexLeft/>
                        <IndexRight/>
                    </Layout>
                </IndexContainerContext.Provider>
            </ConfigProvider>
        );
    }

    updateRootStatus(deltaInfo: IndexContainerState) {
        this.setState(deltaInfo);
    }
}