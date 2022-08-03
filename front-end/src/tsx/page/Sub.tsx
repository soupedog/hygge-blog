import * as React from "react"
import {ErrorInfo} from "react"
import {LogHelper} from '../utils/LogHelper';

import '../../css/default.css';
import '../../css/index.less';
import '../../css/index.scss';

import {ThemeContext} from '../component/MyContext';

// 描述该组件 props 数据类型
export interface IndexContainerProps {
    compiler?: string;
    framework?: string;
}

// 描述该组件 states 数据类型
export interface IndexContainerStatus {
    hasError?: boolean;
}

export class Sub extends React.Component<IndexContainerProps, IndexContainerStatus> {
    constructor(props: any) {
        super(props);
        this.state = {hasError: false};
        LogHelper.info("Sub", "constructor", "----------", false);
    }

    static getDerivedStateFromProps(nextProps?: IndexContainerProps, nextState?: IndexContainerStatus) {
        LogHelper.info("Sub", "getDerivedStateFromProps", "----------", false);
        LogHelper.debug("Sub", "getDerivedStateFromProps-nextProps", nextProps, true);
        LogHelper.debug("Sub", "getDerivedStateFromProps-nextState", nextState, true);
        return nextProps;
    }

    render() {
        if (this.state.hasError) {
            // 你可以渲染任何自定义的降级 UI
            return <h1>Something went wrong.</h1>;
        }
        return (
            <ThemeContext.Consumer>
                {({a, b}) => (
                    <>
                        <h5 style={{height: "2000px", backgroundColor: "blue"}}>a:{a}</h5>
                        <h5>b:{b}</h5>
                    </>
                )}
            </ThemeContext.Consumer>
        );
    }

    shouldComponentUpdate(nextProps?: IndexContainerProps, nextState?: IndexContainerStatus, nextContext?: any) {
        LogHelper.info("Sub", "shouldComponentUpdate", "----------", false);
        LogHelper.debug("Sub", "shouldComponentUpdate-nextProps", nextProps, true);
        LogHelper.debug("Sub", "shouldComponentUpdate-nextState", nextState, true);
        LogHelper.debug("Sub", "shouldComponentUpdate-nextContext", nextContext, true);
        return true;
    }

    async componentDidMount() {
        document.querySelectorAll("h5").forEach((item) => {
            item.style.color = "";
        })

        let value = 0;

        while (value < 300) {
            window.scrollTo({top: value})
            await this.sleep(10);
            value = value + 20
        }


        LogHelper.info("Sub", "componentDidMount", "----------", false);
    }

    getSnapshotBeforeUpdate(prevProps?: IndexContainerProps, prevState?: IndexContainerStatus) {
        LogHelper.info("Sub", "getSnapshotBeforeUpdate", "----------", false);
        LogHelper.debug("Sub", "getSnapshotBeforeUpdate-prevProps", prevProps, true);
        LogHelper.debug("Sub", "getSnapshotBeforeUpdate-prevState", prevState, true);
        return null;
    }

    componentDidUpdate(prevProps?: IndexContainerProps, prevState?: IndexContainerStatus, snapshot?: any) {
        LogHelper.info("Sub", "componentDidUpdate", "----------", false);
        LogHelper.debug("Sub", "componentDidUpdate-prevProps", prevProps, true);
        LogHelper.debug("Sub", "componentDidUpdate-prevState", prevState, true);
        LogHelper.debug("Sub", "componentDidUpdate-snapshot", snapshot, true);
    }

    static getDerivedStateFromError(error?: Error) {
        // 更新 state 使下一次渲染可以显示降级 UI
        return {hasError: true};
    }

    componentDidCatch(error?: Error, info?: ErrorInfo) {
        LogHelper.info("Sub", "componentDidCatch", "----------", false);
        LogHelper.debug("Sub", "componentDidCatch-error", error, true);
        LogHelper.debug("Sub", "componentDidCatch-info", info, true);
    }

    sleep(t: number) {
        return new Promise(resolve => setTimeout(resolve, t));
    }
}