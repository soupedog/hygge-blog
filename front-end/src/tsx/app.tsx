import React from 'react';
import {createRoot} from 'react-dom/client';
import {BrowserRouter, Route, Routes} from "react-router-dom";

import "../style/default.css"
import "../style/markdownCustomStyle.less"
import "highlight.js/styles/atom-one-dark-reasonable.css"
import "katex/dist/katex.min.css"

import {PropertiesHelper} from "./utils/UtilContainer";
import {Modal} from "antd";
import isMobile from "rc-util/es/isMobile"
import ArticleEditor from "./v2/page/ArticleEditor";
import Index from "./v2/page/Index";
import NotFound from "./v2/page/NotFound";
import SignIn from "./v2/page/SignIn";
import Browser from "./v2/page/Browser";

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
    let root = createRoot(container);

    root.render(
        // <React.StrictMode>
        <BrowserRouter>
            <Routes>
                <Route path={"/"} element={<Index key={"index"}/>}/>
                <Route path={"/browser/:aid"} element={<Browser key={"browser"}/>}/>
                <Route path={"/signin"} element={<SignIn key={"signin"}/>}/>
                <Route path={"/editor/article"} element={<ArticleEditor key={"editor-article"}/>}/>
                <Route path={"/editor/article/:aid"} element={<ArticleEditor key={"editor-article"}/>}/>
                <Route path={"/editor/quote/"} element={<ArticleEditor key={"editor-quote"}/>}/>
                <Route path={"/editor/quote/:quoteId"} element={<ArticleEditor key={"editor-quote"}/>}/>
                <Route path={"*"} element={<NotFound key={"notFound"}/>}/>
            </Routes>
        </BrowserRouter>
        // </React.StrictMode>
    );
}