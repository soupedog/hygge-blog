import React, {createContext, useEffect, useMemo, useState} from 'react';
import zhCN from "antd/locale/zh_CN";
import {ConfigProvider} from "antd";
import {AntdTreeNodeInfo} from "../component/markdown/util/MdHelper";
import ArticleEditorForm from "../component/editor/ArticleEditorForm";
import DefaultMarkdownEditor from "../component/markdown/DefaultMarkdownEditor";

export interface ArticleEditorState {
    content: string;
    updateContent: Function;
    tocEnable: boolean;
    updateTocEnable: Function;
    tocTree: AntdTreeNodeInfo[];
    updateTocTree: Function;
}

function ArticleEditor() {
    const [content, updateContent] = useState("");
    const [tocEnable, updateTocEnable] = useState(false);
    const [tocTree, updateTocTree] = useState([]);

    const state = useMemo(() => ({
        content: content,
        updateContent: updateContent,
        tocEnable: tocEnable,
        updateTocEnable: updateTocEnable,
        tocTree: tocTree,
        updateTocTree: updateTocTree,
    }), [content, tocEnable, tocTree]);

    useEffect(() => {
        // 改页面标题
        document.title = "编辑文章";
        // 依赖静态值表示仅初始化时调用一次
    }, []);

    return (
        <ConfigProvider locale={zhCN}>
            <ArticleEditorContext.Provider value={state}>
                <DefaultMarkdownEditor content={content} updateContent={updateContent}/>
                {/*<EditorMenu updateContent={updateContent} tocEnable={tocEnable}*/}
                {/*            updateTocEnable={updateTocEnable} updateTocTree={updateTocTree}/>*/}
                {/*<EditorView content={content} updateContent={updateContent}*/}
                {/*            tocEnable={tocEnable} tocTree={tocTree}/>*/}
                <ArticleEditorForm updateContent={updateContent}/>
            </ArticleEditorContext.Provider>
        </ConfigProvider>
    );
}

export const ArticleEditorContext = createContext<ArticleEditorState>({} as ArticleEditorState);
export default ArticleEditor;