import React, {useEffect} from "react";
import {MdEditor} from "md-editor-rt";
import {ExportPDF, Mark} from "@vavt/rt-extension";
import MarkIcon from "./MarkIcon";
import {ChangeEvent, UploadImgCallBack} from "md-editor-rt/lib/types/MdEditor/type";
import {message} from "antd";
import {key_editor_draft} from "../properties/PropertiesKey";
import {AxiosResponse} from "axios";
import {FileInfo, FileService, HyggeResponse} from "../../rest/ApiClient";
import {UrlHelper} from "../../util/UtilContainer";

export interface DefaultMarkdownEditorProps {
    cid: string,
    content: string,
    updateContent: Function;
}

function DefaultMarkdownEditor({cid, content, updateContent}: DefaultMarkdownEditorProps) {
    const onUploadImg = async (files: Array<File>, callBack: UploadImgCallBack) => {

        if (cid != null && cid.length > 0) {
            const responseTemp: AxiosResponse<HyggeResponse<FileInfo[]>>[] = await Promise.all(
                files.map((file) => {
                    return new Promise<AxiosResponse<HyggeResponse<FileInfo[]>>>((rev, rej) => {
                        const formData = new FormData();
                        formData.append('files', file);
                        let type = "ARTICLE";
                        FileService.uploadFilesPromise(type, formData, cid)
                            .then((response) => rev(response));
                    });
                })
            );

            responseTemp.map(axiosResponse => {
                let fileInfoList = axiosResponse.data.main;
                if (fileInfoList) {
                    callBack(
                        fileInfoList.map(fileInfo => ({
                            url: fileInfo.fileCopyType === "NGINX" ? UrlHelper.getBaseStaticSourceUrl() + fileInfo.src : UrlHelper.getBaseUrl() + "file/" + fileInfo.fileNo,
                            alt: fileInfo.name,
                            title: fileInfo.name,
                        }))
                    );
                }
            });
        } else {
            message.warning("文章创建完成后才可上传图片。");
        }
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