import * as React from "react"
import {LogHelper} from '../utils/LogHelper';

// 描述该组件 props 数据类型
export interface IndexContainerProps {
}

// 描述该组件 states 数据类型
export interface IndexContainerStatus {
}

export class IndexContainer extends React.Component<IndexContainerProps, IndexContainerStatus> {
    constructor(props: IndexContainerProps) {
        super(props);
        this.state = {};
        LogHelper.info("IndexContainer", "constructor", "----------", false);
    }

    render() {
        return (
            <>
                <h1>首页</h1>
            </>
        );
    }
}