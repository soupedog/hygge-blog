import * as React from "react"
import {ErrorInfo} from "react"
import {LogHelper} from './utils/LogHelper';

import '../css/default.css';
import '../css/index.less';
import '../css/index.scss';

// 描述该组件 props 数据类型
export interface IndexContainerProps {
    compiler?: string;
    framework?: string;
}

// 描述该组件 states 数据类型
export interface IndexContainerStatus {
    hasError?: boolean;
}

export class Template extends React.Component<IndexContainerProps, IndexContainerStatus> {
    constructor(props: any) {
        super(props);
        this.state = {hasError: false};
        LogHelper.info("Template", "constructor", "----------", false);
    }

    static getDerivedStateFromProps(nextProps?: IndexContainerProps, nextState?: IndexContainerStatus) {
        LogHelper.info("Template", "getDerivedStateFromProps", "----------", false);
        LogHelper.debug("Template", "getDerivedStateFromProps-nextProps", nextProps, true);
        LogHelper.debug("Template", "getDerivedStateFromProps-nextState", nextState, true);
        return nextProps;
    }

    render() {
        if (this.state.hasError) {
            // 你可以渲染任何自定义的降级 UI
            return <h1>Something went wrong.</h1>;
        }
        return (
            <>
                <h1 className="testStyle1">Hello world - css.</h1>
                <h1 className="testStyle2">Hello world - less.</h1>
                <h1 className="testStyle3">Hello world - scss.</h1>
                <div id="playerContainer"/>
                <div id="md"></div>
            </>
        );
    }

    shouldComponentUpdate(nextProps?: IndexContainerProps, nextState?: IndexContainerStatus, nextContext?: any) {
        LogHelper.info("Template", "shouldComponentUpdate", "----------", false);
        LogHelper.debug("Template", "shouldComponentUpdate-nextProps", nextProps, true);
        LogHelper.debug("Template", "shouldComponentUpdate-nextState", nextState, true);
        LogHelper.debug("Template", "shouldComponentUpdate-nextContext", nextContext, true);
        return true;
    }

    componentDidMount() {
        LogHelper.info("Template", "componentDidMount", "----------", false);
    }

    getSnapshotBeforeUpdate(prevProps?: IndexContainerProps, prevState?: IndexContainerStatus) {
        LogHelper.info("Template", "getSnapshotBeforeUpdate", "----------", false);
        LogHelper.debug("Template", "getSnapshotBeforeUpdate-prevProps", prevProps, true);
        LogHelper.debug("Template", "getSnapshotBeforeUpdate-prevState", prevState, true);
        return null;
    }

    componentDidUpdate(prevProps?: IndexContainerProps, prevState?: IndexContainerStatus, snapshot?: any) {
        LogHelper.info("Template", "componentDidUpdate", "----------", false);
        LogHelper.debug("Template", "componentDidUpdate-prevProps", prevProps, true);
        LogHelper.debug("Template", "componentDidUpdate-prevState", prevState, true);
        LogHelper.debug("Template", "componentDidUpdate-snapshot", snapshot, true);
    }

    static getDerivedStateFromError(error?: Error) {
        // 更新 state 使下一次渲染可以显示降级 UI
        return {hasError: true};
    }

    componentDidCatch(error?: Error, info?: ErrorInfo) {
        LogHelper.info("Template", "componentDidCatch", "----------", false);
        LogHelper.debug("Template", "componentDidCatch-error", error, true);
        LogHelper.debug("Template", "componentDidCatch-info", info, true);
    }
}