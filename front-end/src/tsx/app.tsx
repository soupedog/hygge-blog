import React from 'react';
import {createRoot} from 'react-dom/client';
import {BrowserRouter, Route, Routes} from "react-router-dom";
import {PropertiesHelper} from "./utils/UtilContainer";
import {Modal} from "antd";
import isMobile from "rc-util/es/isMobile"
import Editor from "./v2/page/Editor";
import Index from "./v2/page/Index";

import "../style/markdownCustomStyle.less"
import "highlight.js/styles/atom-one-dark-reasonable.css"
import "katex/dist/katex.min.css"
import "../style/default.css"

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
        <BrowserRouter>
            <Routes>
                <Route path={"/"} element={<Index key={"index"}/>}/>
                <Route path={"/browser/:aid"} element={<Editor key={"browser"}/>}/>
                <Route path={"/signin"} element={<Editor key={"signin"}/>}/>
                <Route path={"/signin/auto"} element={<Editor key={"signin-auto"}/>}/>
                <Route path={"/editor/article"} element={<Editor key={"editor-article"}/>}/>
                <Route path={"/editor/article/:aid"} element={<Editor key={"editor-article"}/>}/>
                <Route path={"/editor/quote/"} element={<Editor key={"editor-quote"}/>}/>
                <Route path={"/editor/quote/:quoteId"} element={<Editor key={"editor-quote"}/>}/>
                <Route path={"*"} element={<Editor key={"notFound"}/>}/>
            </Routes>
        </BrowserRouter>
        // </React.StrictMode>
    );
}