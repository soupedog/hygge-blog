import * as React from "react"
import {LogHelper} from '../utils/LogHelper';

// 描述该组件 props 数据类型
export interface EditQuoteContainerProps {
}

// 描述该组件 states 数据类型
export interface EditQuoteContainerStatus {
}

export class EditQuoteContainer extends React.Component<EditQuoteContainerProps, EditQuoteContainerStatus> {
    constructor(props: EditQuoteContainerProps) {
        super(props);
        this.state = {};
        LogHelper.info("EditQuoteContainer", "constructor", "----------", false);
    }

    render() {
        return (
            <>
                <h1>句子收藏编辑页</h1>
            </>
        );
    }
}