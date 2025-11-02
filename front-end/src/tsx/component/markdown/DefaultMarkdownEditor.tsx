import "@vavt/rt-extension/lib/asset/ExportPDF.css";
import "@vavt/rt-extension/lib/asset/Emoji.css";
import "@vavt/rt-extension/lib/asset/Mark.css";

import React, {useEffect} from "react";
import {config, MdEditor} from "md-editor-rt";
import {ExportPDF, Mark} from "@vavt/rt-extension";
import {keymap} from '@codemirror/view';
// @ts-ignore
import MarkExtension from 'markdown-it-mark';
import MarkIcon from "./MarkIcon";
import {UploadImgCallBack} from "md-editor-rt/lib/types/MdEditor/type";
import {FileInfo, FileService, HyggeResponse} from "../../rest/ApiClient";
import {UrlHelper} from "../../util/UtilContainer";
import {AxiosResponse} from "axios";

export interface DefaultMarkdownEditorProps {
    content: string,
    updateContent: Function;
}

config({
    markdownItConfig(md) {
        md.use(MarkExtension);
    },
    codeMirrorExtensions(extensions, {keyBindings}) {
        // 1. 先把旧的快捷键映射移除
        const newExtensions = [...extensions].filter((item) => {
            return item.type !== 'keymap';
        });

        // 2. 参考快捷键配置的源码，找到 CtrlF 的配置项在 keyBindings 中的位置
        const CtrlF = keyBindings.find((i) => i.key === 'Ctrl-f');

        // 3. 复制 CtrlF 的功能到我自定义的 CtrlL
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

    const onUploadImg = async (files: Array<File>, callBack: UploadImgCallBack) => {
        const responseTemp: AxiosResponse<HyggeResponse<FileInfo[]>>[] = await Promise.all(
            files.map((file) => {
                return new Promise<AxiosResponse<HyggeResponse<FileInfo[]>>>((rev, rej) => {
                    const formData = new FormData();
                    formData.append('files', file);
                    let type = "ARTICLE";
                    FileService.uploadFilesPromise(type, formData)
                        .then((response) => rev(response));
                });
            })
        );

        responseTemp.map(axiosResponse => {
            let fileInfoList = axiosResponse.data.main;
            if (fileInfoList) {
                callBack(
                    fileInfoList.map(fileInfo => ({
                        url: UrlHelper.getBaseStaticSourceUrl() + fileInfo.src,
                        alt: fileInfo.name,
                        title: fileInfo.name,
                    }))
                );
            }
        });
    };

    useEffect(() => {
        // 依赖静态值表示仅初始化时调用一次
    }, []);

    return (
        <MdEditor
            placeholder="开始记录奇思妙想..."
            toolbars={[
                // 第一组图标
                0, "bold", "underline", "italic", "strikeThrough", "-",
                // 第二组图标 "-" 是分隔符
                "title", "sup", "sub", "quote", "unorderedList", "orderedList", "-",
                // 第三组图标 "-" 是分隔符
                "task", "codeRow", "code", "link", "image", "table", "mermaid", "katex", "-",
                // 第四组图标
                "revoke", "next", "save",
                // 第三组图标 "=" 是右对齐  "-" 是分隔符
                "=", "-", "prettier", "previewOnly", 1, "pageFullscreen", "catalog",
            ]}
            defToolbars={[
                <Mark title={"高亮"} key="Mark" trigger={<MarkIcon/>}/>,
                <ExportPDF key="ExportPDF" value={content}/>,
            ]}
            value={content}
            onChange={(nextContent) => updateContent(nextContent)}
            autoFocus={true}
            noPrettier={false}
            showToolbarName={true}
            onSave={(content) => {
                alert(content)
            }}
            onUploadImg={onUploadImg}
        />
    );
}

export default DefaultMarkdownEditor;