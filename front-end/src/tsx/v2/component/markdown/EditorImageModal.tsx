import React, {useState} from 'react';
import {Button, Input, message, Modal, Radio, Space} from "antd";
import {editor_text_area} from "../properties/ElementNameContainer";
import {contentChangeUndoStackHandler} from "./EditorView";
import { ArticleEditorContext } from '../../page/ArticleEditor';
import InputElementHelper from "./util/InputElementHelper";

function EditorImageModal() {
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [imageText, setImageText] = useState("");
    const [imageLinkValue, setImageLinkValue] = useState("");
    const [imageType, setImageType] = useState("center");

    const [messageApi, contextHolder] = message.useMessage();

    const showModal = () => {
        setIsModalOpen(true);
    };

    const handleCancel = () => {
        setIsModalOpen(false);
    };

    return (
        <ArticleEditorContext.Consumer>
            {({updateContent}) => (
                <>
                    {contextHolder}
                    <Button type="link" onClick={showModal}>
                        插入图片
                    </Button>
                    <Modal title="图片信息" open={isModalOpen} onOk={() => {
                        if (imageText.length < 1 || imageLinkValue.length < 1) {
                            messageApi.warning("请输入完整图片信息");
                            return;
                        }

                        let content: string;

                        if (imageType == "center") {
                            content = "<img src=\"" + imageLinkValue + "\" alt=\"" + imageText + "\" width=\"540\" height=\"258\" style=\"margin:0 auto;display: block;\">";
                        } else {
                            content = "![" + imageText + "](" + imageLinkValue + ")";
                        }

                        // @ts-ignore
                        let element: HTMLTextAreaElement = document.getElementById(editor_text_area);
                        InputElementHelper.appendTextToTextArea(element, content, ({
                                                                                       appendTarget,
                                                                                       leftPart,
                                                                                       rightPart
                                                                                   }) => {
                            let nextContent = leftPart + appendTarget + rightPart;
                            updateContent(nextContent);

                            contentChangeUndoStackHandler(nextContent);
                        });

                        // 完成插入后重置为初始状态
                        setIsModalOpen(false);
                        setImageText("");
                        setImageLinkValue("");
                    }} onCancel={handleCancel}>
                        <Space size={"small"} direction={"vertical"} style={{width: "90%"}}>
                            <Input placeholder="提示文本" value={imageText} onChange={(event) => {
                                setImageText(event.target.value);
                            }}/>
                            <Input placeholder="图片链接" value={imageLinkValue} onChange={(event) => {
                                setImageLinkValue(event.target.value);
                            }}/>
                            <Radio.Group defaultValue="center" buttonStyle="solid" onChange={(event) => {
                                setImageType(event.target.value);
                            }}>
                                <Radio.Button value="center">图片居中</Radio.Button>
                                <Radio.Button value="default">Markdown 默认</Radio.Button>
                            </Radio.Group>
                        </Space>
                    </Modal>
                </>
            )}
        </ArticleEditorContext.Consumer>
    );
}

export default EditorImageModal;