import * as React from "react"
import {LogHelper} from '../utils/UtilContainer';

// 描述该组件 props 数据类型
export interface TemplateProps {
}

// 描述该组件 states 数据类型
export interface TemplateStatus {
}

export class Template extends React.Component<TemplateProps, TemplateStatus> {
    constructor(props: TemplateProps) {
        super(props);
        this.state = {};
        LogHelper.info({className: "Template", msg: "初始化成功"});
    }

    render() {
        return (
            <>
            </>
        );
    }
}
