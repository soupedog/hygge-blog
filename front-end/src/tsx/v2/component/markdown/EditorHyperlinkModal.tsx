import React, {useState} from 'react';
import {Button, Input, message, Modal, Space} from "antd";
import {editor_text_area} from "../properties/ElementNameContainer";
import {contentChangeUndoStackHandler} from "./EditorView";
import InputElementHelper from "./util/InputElementHelper";
import { EditorContext } from '../../page/Editor';

function EditorHyperlinkModal() {
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [hyperlinkText, setHyperlinkText] = useState("");
    const [hyperlinkValue, setHyperlinkValue] = useState("");

    const [messageApi, contextHolder] = message.useMessage();

    const showModal = () => {
        setIsModalOpen(true);
    };

    const handleCancel = () => {
        setIsModalOpen(false);
    };

    return (
        <EditorContext.Consumer>
            {({updateContent}) => (
                <>
                    {contextHolder}
                    <Button type="link" onClick={showModal}>
                        插入超链接
                    </Button>
                    <Modal title="超链接信息" open={isModalOpen} onOk={() => {
                        if (hyperlinkText.length < 1 || hyperlinkValue.length < 1) {
                            messageApi.warning("请输入完整超链接信息");
                            return;
                        }

                        let content = "[" + hyperlinkText + "](" + hyperlinkValue + ")";

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
                        setHyperlinkText("");
                        setHyperlinkValue("");
                    }} onCancel={handleCancel}>
                        <Space size={"small"} direction={"vertical"} style={{width: "90%"}}>
                            <Input placeholder="显示文本" value={hyperlinkText} onChange={(event) => {
                                setHyperlinkText(event.target.value);
                            }}/>
                            <Input placeholder="超链接" value={hyperlinkValue} onChange={(event) => {
                                setHyperlinkValue(event.target.value);
                            }}/>
                        </Space>
                    </Modal>
                </>
            )}
        </EditorContext.Consumer>
    );
}

export default EditorHyperlinkModal;