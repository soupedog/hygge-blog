import * as React from "react"
import {LogHelper} from '../utils/LogHelper';

// 描述该组件 props 数据类型
export interface EditArticleContainerProps {
}

// 描述该组件 states 数据类型
export interface EditArticleContainerStatus {
}

export class EditArticleContainer extends React.Component<EditArticleContainerProps, EditArticleContainerStatus> {
    constructor(props: EditArticleContainerProps) {
        super(props);
        this.state = {};
        LogHelper.info("EditArticleContainer", "constructor", "----------", false);
    }

    render() {
        return (
            <>
                <h1>文章编辑页</h1>
            </>
        );
    }
}