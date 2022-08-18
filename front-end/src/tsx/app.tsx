import React from 'react';
import {createRoot} from 'react-dom/client';
import {HashRouter, Route, Routes} from "react-router-dom";
import {IndexContainer} from "./page/IndexContainer";
import ArticleBrowserContainer from "./page/ArticleBrowserContainer";
import {SignInContainer} from "./page/SignInContainer";
import EditQuoteContainer from "./page/EditQuoteContainer";
import EditArticleContainer from "./page/EditArticleContainer";
import {SignInAutoContainer} from "./page/SignInAutoContainer";
import {PropertiesHelper} from "./utils/UtilContainer";
import {Modal} from "antd";
import isMobile from "rc-util/es/isMobile"

let enableClientDeviceWarning: string | null = localStorage.getItem('enableClientDeviceWarning');

if (PropertiesHelper.booleanOfNullable({target: enableClientDeviceWarning, defaultValue: true})) {
    let isPC: boolean = !isMobile();
    if (!isPC) {
        Modal.confirm({
            title: '提示',
            content: '检测到当前设备为移动端，本站未对移动端做特殊适配、优化处理，推荐您使用 PC 访问，若执意使用移动端，产生的不便敬请谅解。',
            okText: "不再提示",
            onOk: () => {
                localStorage.setItem('enableClientDeviceWarning', "false");
            },
            cancelText: "确认",
        });
    }
}

const container: Element | null = document.getElementById('root');

if (container != null) {
    const root = createRoot(container);

    root.render(
        // <React.StrictMode>
        <HashRouter>
            <Routes>
                <Route path={""} element={<IndexContainer key={"index"}/>}/>
                <Route path={"/browser/:aid"} element={<ArticleBrowserContainer key={"browser"}/>}/>
                <Route path={"/signin"} element={<SignInContainer key={"signin"}/>}/>
                <Route path={"/signin/auto"} element={<SignInAutoContainer key={"signin-auto"}/>}/>
                <Route path={"/editor/article"} element={<EditArticleContainer key={"editor-article"}/>}/>
                <Route path={"/editor/article/:aid"} element={<EditArticleContainer key={"editor-article"}/>}/>
                <Route path={"/editor/quote/"} element={<EditQuoteContainer key={"editor-quote"}/>}/>
                <Route path={"/editor/quote/:quoteId"} element={<EditQuoteContainer key={"editor-quote"}/>}/>
            </Routes>
        </HashRouter>
        // </React.StrictMode>
    );
}