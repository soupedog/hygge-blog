import * as React from "react"
import {LogHelper} from '../../../utils/UtilContainer';

import {IndexContainerContext} from "../../context/HyggeContext";
import {IndexLeft} from "./IndexLeft";
import {IndexRight} from "./IndexRight";
import {IndexContainerStatus} from "../../IndexContainer";

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
        let _react = this;
        return (
            <IndexContainerContext.Consumer>
                {(status: IndexContainerStatus) => (
                    <>
                        <IndexLeft/>
                        <IndexRight/>
                    </>
                )}
            </IndexContainerContext.Consumer>
        );
    }
}
