import * as React from "react"
import {LogHelper} from '../utils/UtilContainer';

// 描述该组件 props 数据类型
export interface ArticleBrowserContainerProps {
}

// 描述该组件 states 数据类型
export interface ArticleBrowserContainerStatus {
}

export class ArticleBrowserContainer extends React.Component<ArticleBrowserContainerProps, ArticleBrowserContainerStatus> {
    constructor(props: ArticleBrowserContainerProps) {
        super(props);
        this.state = {};
        LogHelper.info({className: "ArticleBrowserContainer", msg: "初始化成功"});
    }

    render() {
        return (
            <>
                <h1>文章浏览页</h1>
            </>
        );
    }
}