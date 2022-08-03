import * as React from "react"
import {ErrorInfo} from "react"
import {LogHelper} from '../utils/LogHelper';

import '../../css/default.css';
import '../../css/index.less';
import '../../css/index.scss';

import {ICCCCC, ThemeContext} from '../component/MyContext';
import {Sub} from "./Sub";

// 描述该组件 props 数据类型
export interface IndexContainerProps {
    compiler?: string;
    framework?: string;
}

// 描述该组件 states 数据类型
export interface IndexContainerStatus {
    hasError?: boolean;
    context: ICCCCC;
}

export class Main extends React.Component<IndexContainerProps, IndexContainerStatus> {
    constructor(props: IndexContainerProps) {
        super(props);
        this.state = {hasError: false, context: {a: 1, b: "asd"}};
        LogHelper.info("Main", "constructor", "----------", false);
    }

    static getDerivedStateFromProps(nextProps: Readonly<IndexContainerProps>, nextState: Readonly<IndexContainerStatus>) {
        LogHelper.info("Main", "getDerivedStateFromProps", "----------", false);
        LogHelper.debug("Main", "getDerivedStateFromProps-nextProps", nextProps, true);
        LogHelper.debug("Main", "getDerivedStateFromProps-nextState", nextState, true);
        return nextProps;
    }

    render() {
        if (this.state.hasError) {
            // 你可以渲染任何自定义的降级 UI
            return <h1>Something went wrong.</h1>;
        }
        return (
            <ThemeContext.Provider value={this.state.context}>
                <Sub/>
            </ThemeContext.Provider>
        );
    }

    shouldComponentUpdate(nextProps: Readonly<IndexContainerProps>, nextState: Readonly<IndexContainerStatus>, nextContext: any) {
        LogHelper.info("Main", "shouldComponentUpdate", "----------", false);
        LogHelper.debug("Main", "shouldComponentUpdate-nextProps", nextProps, true);
        LogHelper.debug("Main", "shouldComponentUpdate-nextState", nextState, true);
        LogHelper.debug("Main", "shouldComponentUpdate-nextContext", nextContext, true);
        return true;
    }

    componentDidMount() {
        let _react = this
        setTimeout(() => {
            console.log("开始修改")

            _react.setState({context: {a: 2}})
        }, 2000)

        LogHelper.info("Main", "componentDidMount", "----------", false);
    }

    getSnapshotBeforeUpdate(prevProps: Readonly<IndexContainerProps>, prevState: Readonly<IndexContainerStatus>) {
        LogHelper.info("Main", "getSnapshotBeforeUpdate", "----------", false);
        LogHelper.debug("Main", "getSnapshotBeforeUpdate-prevProps", prevProps, true);
        LogHelper.debug("Main", "getSnapshotBeforeUpdate-prevState", prevState, true);
        return null;
    }

    componentDidUpdate(prevProps: Readonly<IndexContainerProps>, prevState: Readonly<IndexContainerStatus>, snapshot?: any) {
        LogHelper.info("Main", "componentDidUpdate", "----------", false);
        LogHelper.debug("Main", "componentDidUpdate-prevProps", prevProps, true);
        LogHelper.debug("Main", "componentDidUpdate-prevState", prevState, true);
        LogHelper.debug("Main", "componentDidUpdate-snapshot", snapshot, true);
    }

    static getDerivedStateFromError(error?: Error) {
        // 更新 state 使下一次渲染可以显示降级 UI
        console.log(error)
        return {hasError: true};
    }

    componentDidCatch(error?: Error, info?: ErrorInfo) {
        LogHelper.info("Main", "componentDidCatch", "----------", false);
        LogHelper.debug("Main", "componentDidCatch-error", error, true);
        LogHelper.debug("Main", "componentDidCatch-info", info, true);
    }
}