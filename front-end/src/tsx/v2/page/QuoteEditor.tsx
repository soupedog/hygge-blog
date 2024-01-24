import React, {createContext, useMemo, useState} from 'react';
import zhCN from "antd/locale/zh_CN";
import {ConfigProvider} from "antd";
import EditorMenu from "../component/markdown/EditorMenu";
import EditorView from "../component/markdown/EditorView";
import QuoteEditorForm from "../component/editor/QuoteEditorForm";
import {AntdTreeNodeInfo} from "../component/markdown/util/MdHelper";

export interface QuoteEditorState {
    content: string;
    updateContent: Function;
    tocEnable: boolean;
    updateTocEnable: Function;
    tocTree: AntdTreeNodeInfo[];
    updateTocTree: Function;
}

function QuoteEditor() {
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
            <QuoteEditorContext.Provider value={state}>
                <EditorMenu updateContent={updateContent} tocEnable={tocEnable}
                            updateTocEnable={updateTocEnable} updateTocTree={updateTocTree}/>
                <EditorView content={content} updateContent={updateContent}
                            tocEnable={tocEnable} tocTree={tocTree}/>
                <QuoteEditorForm updateContent={updateContent}/>
            </QuoteEditorContext.Provider>
        </ConfigProvider>
    );
}

export const QuoteEditorContext = createContext<QuoteEditorState>({} as QuoteEditorState);
export default QuoteEditor;