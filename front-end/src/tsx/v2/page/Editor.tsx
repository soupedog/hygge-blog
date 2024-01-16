import React, {createContext, useMemo, useState} from 'react';
import zhCN from "antd/locale/zh_CN";
import {ConfigProvider} from "antd";
import EditorMenu from "../component/markdown/EditorMenu";
import EditorView from "../component/markdown/EditorView";
import {AntdTreeNodeInfo} from "../component/markdown/util/MdHelper";
import ArticleEditorForm from "../component/editor/ArticleEditorForm";

export interface EditorState {
    content: string;
    updateContent: Function;
    tocEnable: boolean;
    updateTocEnable: Function;
    tocTree: AntdTreeNodeInfo[];
    updateTocTree: Function;
}

function Editor() {
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

    return (
        <ConfigProvider locale={zhCN}>
            <EditorContext.Provider value={state}>
                <EditorMenu/>
                <EditorView/>
                <ArticleEditorForm updateContent={updateContent}/>
            </EditorContext.Provider>
        </ConfigProvider>
    );
}

export const EditorContext = createContext<EditorState>({} as EditorState);
export default Editor;