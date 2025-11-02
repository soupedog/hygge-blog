import "@vavt/rt-extension/lib/asset/ExportPDF.css";
import "@vavt/rt-extension/lib/asset/Emoji.css";
import "@vavt/rt-extension/lib/asset/Mark.css";

import React, {useEffect} from "react";
import {config, MdEditor} from "md-editor-rt";
import {ExportPDF, Mark} from "@vavt/rt-extension";
// @ts-ignore
import MarkExtension from 'markdown-it-mark';
import MarkIcon from "./MarkIcon";
import {keymap} from '@codemirror/view';

export interface DefaultMarkdownEditorProps {
    content: string,
    updateContent: Function;
}

config({
    markdownItConfig(md) {
        md.use(MarkExtension);
    },
    codeMirrorExtensions(extensions, {keyBindings}) {
        // 1. 先把默认的快捷键扩展移除
        const newExtensions = [...extensions].filter((item) => {
            return item.type !== 'keymap';
        });

        // 2. 参考快捷键配置的源码，找到CtrlB的配置项在keyBindings中的位置
        const CtrlF = keyBindings.find((i) => i.key === 'Ctrl-f');

        // 3. 配置codemirror快捷键的文档
        // https://codemirror.net/docs/ref/#commands
        const MyCtrlL = {
            ...CtrlF,
            key: 'Ctrl-l',
            mac: 'Cmd-l',
        };

        // 4. 把修改后的快捷键放到待构建扩展的数组中
        const newKeyBindings = [MyCtrlL, ...keyBindings.filter((i) => i.key !== 'Ctrl-l')];

        newExtensions.push({
            type: 'newKeymap',
            extension: keymap.of(newKeyBindings)
        });

        return newExtensions;
    }
});

function DefaultMarkdownEditor({content, updateContent}: DefaultMarkdownEditorProps) {

    useEffect(() => {
        // 依赖静态值表示仅初始化时调用一次
    }, []);

    return (
        <MdEditor
            noPrettier={false}
            value={content}
            onChange={(nextContent) => updateContent(nextContent)}
            placeholder="开始记录奇思妙想..."
            onSave={(da) => {
                alert(da)
            }}
            autoFocus={true}
            showToolbarName={true}
            // // onDrop={(e) => handleDrop(e)}
            // // onUploadImg={handleUploadImage}
            toolbars={[
                // 第一组图标
                1, "bold", "underline", "italic", "strikeThrough", "-",
                // 第二组图标 "-" 是分隔符
                "title", "sup", "sub", "quote", "unorderedList", "orderedList", "-",
                // 第三组图标 "-" 是分隔符
                "task", "codeRow", "code", "link", "image", "table", "mermaid", "katex", "-",
                // 第四组图标
                // "revoke", "next", "save",
                "save",
                // 第三组图标 "=" 是右对齐  "-" 是分隔符
                "=", "-", 0, "prettier", "pageFullscreen", "previewOnly", "catalog"
            ]}
            defToolbars={[
                <ExportPDF key="ExportPDF" value={content}/>,
                <Mark title={"高亮"} key="Mark" trigger={<MarkIcon/>}/>
            ]}
            // footers={["markdownTotal", "=", "scrollSwitch"]}
            // defFooters={[<span>{currentTime}</span>]}
        />
    );
}

export default DefaultMarkdownEditor;