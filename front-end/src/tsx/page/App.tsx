import * as React from "react"
import {LogHelper} from '../utils/LogHelper';

import {Route, Routes} from "react-router-dom";
import {IndexContainer} from "./IndexContainer";
import {ArticleBrowserContainer} from "./ArticleBrowserContainer";
import {EditArticleContainer} from "./EditArticleContainer";
import {EditQuoteContainer} from "./EditQuoteContainer";
import {SignInContainer} from "./SignInContainer";

import '../../css/default.css';

// 描述该组件 props 数据类型
export interface AppProps {
}

// 描述该组件 states 数据类型
export interface AppStatus {
}

export class App extends React.Component<AppProps, AppStatus> {
    constructor(props: AppProps) {
        super(props);
        this.state = {};
        LogHelper.info("App", "constructor", "----------", false);
    }

    render() {
        return (
            <Routes>
                <Route path={""} element={<IndexContainer/>}/>
                <Route path={"/browser"} element={<ArticleBrowserContainer/>}/>
                <Route path={"/signin"} element={<SignInContainer/>}/>
                <Route path={"/edit/article"} element={<EditArticleContainer/>}/>
                <Route path={"/edit/quote"} element={<EditQuoteContainer/>}/>
            </Routes>
        );
    }
}