import React, {useEffect} from "react";
import {MdEditor} from "md-editor-rt";
import {ExportPDF, Mark} from "@vavt/rt-extension";
import MarkIcon from "./MarkIcon";
import {ChangeEvent, UploadImgCallBack} from "md-editor-rt/lib/types/MdEditor/type";
import {FileInfo, FileService, HyggeResponse} from "../../rest/ApiClient";
import {UrlHelper} from "../../util/UtilContainer";
import {AxiosResponse} from "axios";
import {message} from "antd";
import {key_editor_draft} from "../properties/PropertiesKey";

export interface DefaultMarkdownEditorProps {
    content: string,
    updateContent: Function;
}

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
            onChange={updateContent as ChangeEvent}
            autoFocus={true}
            noPrettier={false}
            showToolbarName={true}
            onSave={(content) => {
                if (content == null || content.length < 1) {
                    localStorage.removeItem(key_editor_draft);
                    message.warning("已将本地草稿清空！");
                } else {
                    localStorage.setItem(key_editor_draft, content);
                    message.info("已将草稿保存到本地。");
                }
            }}
            onUploadImg={onUploadImg}
        />
    );
}

export default DefaultMarkdownEditor;