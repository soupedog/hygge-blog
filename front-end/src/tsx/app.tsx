import "../style/default.css"
import "../style/markdownCustomStyle.less"
import "md-editor-rt/lib/style.css";
import "@vavt/rt-extension/lib/asset/ExportPDF.css";
import "@vavt/rt-extension/lib/asset/Mark.css";
import 'highlight.js/styles/atom-one-dark.css';

import React from 'react';
import {createRoot} from 'react-dom/client';
import {BrowserRouter, Route, Routes} from "react-router-dom";
import {message, Modal} from "antd";
import '@ant-design/v5-patch-for-react-19';
import isMobile from "rc-util/es/isMobile"
import ArticleEditor from "./page/ArticleEditor";
import Index from "./page/Index";
import NotFound from "./page/NotFound";
import SignIn from "./page/SignIn";
import Browser from "./page/Browser";
import QuoteEditor from "./page/QuoteEditor";
import FileManage from "./page/FileManage";
import {PropertiesHelper} from "./util/UtilContainer";
import FileOperation from "./page/FileOperation";
import {config} from "md-editor-rt";
// @ts-ignore
import MarkExtension from "markdown-it-mark";
import * as prettier from 'prettier';
import parserMarkdown from 'prettier/plugins/markdown';
import {keymap} from "@codemirror/view";
import highlight from 'highlight.js';
import mermaid from 'mermaid';

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

// Markdown 工具全局配置
config({
    markdownItConfig(md) {
        md.use(MarkExtension);
    },
    editorExtensions: {
        prettier: {
            prettierInstance: prettier,
            parserMarkdownInstance: parserMarkdown
        },
        highlight: {
            instance: highlight
        },
        mermaid: {
            instance: mermaid
        }
    },
    codeMirrorExtensions(extensions, {keyBindings}) {
        // 1. 先把旧的快捷键映射移除
        const newExtensions = [...extensions].filter((item) => {
            return item.type !== 'keymap';
        });

        // 2. 参考快捷键配置的源码，找到 CtrlF 的配置项在 keyBindings 中的位置
        const CtrlF = keyBindings.find((i) => i.key === 'Ctrl-f');

        const prettierAction = CtrlF!.shift

        // 3. 把美化操作绑定到 ctrl + alt + L 上
        // https://codemirror.net/docs/ref/#commands
        const MyCtrlL = {
            key: 'Ctrl-l',
            mac: 'Cmd-l',
            // @ts-ignore
            any(_view, e) {
                if ((e.ctrlKey || e.metaKey) && e.altKey && e.code === 'KeyL') {
                    // @ts-ignore
                    prettierAction();
                    message.success("已将文本进行美化排版。")
                }
            }
        };

        // 4. 把修改后的快捷键放到待构建扩展的数组中
        const newKeyBindings = [MyCtrlL, ...keyBindings.filter((i) => i.key !== 'Ctrl-l')];

        newExtensions.push({
            type: 'newKeymap',
            // @ts-ignore
            extension: keymap.of(newKeyBindings)
        });

        return newExtensions;
    }
});


const container: Element | null = document.getElementById('root');

if (container != null) {
    let root = createRoot(container);

    root.render(
        // <React.StrictMode>  (仅本地调试用一用)
        // 会使得每个组件在开发模式下被额外渲染两次，通过多次渲染，React 能够确保副作用代码是幂等的，即无论组件渲染多少次，副作用的结果都是一致的。
        <BrowserRouter
            future={{
                v7_startTransition: true,
                v7_relativeSplatPath: true
            }}
        >
            <Routes>
                <Route path={"/"} element={<Index key={"index"}/>}/>
                <Route path={"/browser/:aid"} element={<Browser key={"browser"}/>}/>
                <Route path={"/signin"} element={<SignIn key={"signin"}/>}/>
                <Route path={"/editor/article"} element={<ArticleEditor key={"editor-article"}/>}/>
                <Route path={"/editor/article/:aid"} element={<ArticleEditor key={"editor-article"}/>}/>
                <Route path={"/editor/quote/"} element={<QuoteEditor key={"editor-quote"}/>}/>
                <Route path={"/editor/quote/:quoteId"} element={<QuoteEditor key={"editor-quote"}/>}/>
                <Route path={"/file/manage"} element={<FileManage key={"file-manage"}/>}/>
                <Route path={"/file/operation"} element={<FileOperation key={"file-operation"}/>}/>
                <Route path={"/file/operation/:fileNo"} element={<FileOperation key={"file-operation"}/>}/>
                <Route path={"*"} element={<NotFound key={"notFound"}/>}/>
            </Routes>
        </BrowserRouter>
        // </React.StrictMode>
    );
}