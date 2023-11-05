import React, {useState} from 'react';
import {Button, Input, message, Modal, Radio, Space} from "antd";
import {editor_text_area} from "../properties/ElementNameContainer";
import {contentChangeUndoStackHandler} from "./EditorView";
import InputElementHelper from "./util/InputElementHelper";
import { EditorContext } from '../../page/Editor';

function EditorBilibiliShareModal() {
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [isAutoPlay, setIsAutoPlay] = useState(0);
    const [bvid, setBvid] = useState("");

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
                        Bilibili 外链
                    </Button>
                    <Modal title="外链信息" open={isModalOpen} onOk={() => {
                        if (bvid.length < 1) {
                            messageApi.warning("请输入完整 bvid 信息");
                            return;
                        }

                        let content: string = "<iframe src='//player.bilibili.com/player.html?autoplay=" + isAutoPlay + "&bvid=" + bvid + "' border='0' framespacing='0' allowfullscreen={true} style='width:640px;height:360px;display:block;margin: 0 auto' />";

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
                    }} onCancel={handleCancel}>
                        <Space size={"small"} direction={"vertical"} style={{width: "90%"}}>
                            <Input placeholder="BV 号" value={bvid} onChange={(event) => {
                                setBvid(event.target.value);
                            }}/>
                            <Radio.Group defaultValue="0" buttonStyle="solid" onChange={(event) => {
                                setIsAutoPlay(event.target.value);
                            }}>
                                <Radio.Button value="0">禁用自动播放</Radio.Button>
                                <Radio.Button value="1">自动播放</Radio.Button>
                            </Radio.Group>
                        </Space>
                    </Modal>
                </>
            )}
        </EditorContext.Consumer>
    );
}

export default EditorBilibiliShareModal;