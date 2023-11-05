import React from 'react';
import {Button, message, Row, Space, Tabs, Tooltip, Upload} from "antd";
import {
    key_draft,
    md_template_acronym,
    md_template_code,
    md_template_scheduled_tasks,
    md_template_summary,
    md_template_table
} from "./properties/MarkDownStaticValue";
import EditorHyperlinkModal from "./EditorHyperlinkModal";
import {editor_text_area} from "../properties/ElementNameContainer";
import EditorImageModal from "./EditorImageModal";
import {contentChangeUndoStackHandler} from "./EditorView";
import EditorBilibiliShareModal from "./EditorBilibiliShareModal";
import {UploadOutlined} from '@ant-design/icons';
import { EditorContext } from '../../page/Editor';
import {AntdTreeNodeInfo, MdHelper} from "./util/MdHelper";
import InputElementHelper from "./util/InputElementHelper";

const onChange = (key: string) => {
    console.log(key);
};

function EditorMenu() {

    return (
        <EditorContext.Consumer>
            {({updateContent, tocEnable, updateTocEnable, updateTocTree}) => (
                <Tabs defaultActiveKey="1" items={[
                    {
                        key: '1',
                        label: '　　常规　　',
                        children:
                            <Row gutter={[8, 8]}>
                                <Space size={"small"}>
                                    <Button type="link" onClick={(event) => {

                                        let antdTreeNodeInfos: Array<AntdTreeNodeInfo> = new Array<AntdTreeNodeInfo>();
                                        let map: Map<number, AntdTreeNodeInfo> = new Map<number, AntdTreeNodeInfo>();

                                        document.querySelectorAll("h1,h2,h3,h4,h5,h6").forEach((item, index) => {
                                            let antdTreeNode = {
                                                index: index,
                                                key: "toc_" + index,
                                                nodeName: item.tagName,
                                                level: null,
                                                title: item.textContent as string,
                                                value: item.id,
                                                parentNodeIndex: null,
                                                children: new Array<AntdTreeNodeInfo>
                                            };

                                            antdTreeNodeInfos.push(antdTreeNode);
                                            map.set(index, antdTreeNode);
                                        });

                                        let currentTOC = MdHelper.initTitleTree({
                                            currentTOCArray: antdTreeNodeInfos,
                                            allTocNodeMap: map,
                                            errorCallback: null
                                        });

                                        updateTocTree(currentTOC);

                                        console.log(currentTOC);

                                        if (currentTOC.length > 0) {
                                            updateTocEnable(!tocEnable);
                                        } else {
                                            message.info("未找到目录结构");
                                        }
                                    }}>{tocEnable ? "隐藏目录" : "预览目录"}</Button>
                                    <Tooltip placement="top" title={"Ctrl + B"}>
                                        <Button type="link" onClick={(event) => {
                                            // @ts-ignore
                                            let element: HTMLTextAreaElement = document.getElementById(editor_text_area);
                                            InputElementHelper.appendTextToTextArea(element, "", ({
                                                                                                      leftPart,
                                                                                                      selectedPart,
                                                                                                      rightPart
                                                                                                  }) => {
                                                let nextContent = leftPart + "**" + selectedPart + "**" + rightPart;
                                                updateContent(nextContent);

                                                contentChangeUndoStackHandler(nextContent);
                                            });
                                        }}>加粗</Button>
                                    </Tooltip>
                                    <Tooltip placement="top" title={"Ctrl + I"}>
                                        <Button type="link" onClick={(event) => {
                                            // @ts-ignore
                                            let element: HTMLTextAreaElement = document.getElementById(editor_text_area);
                                            InputElementHelper.appendTextToTextArea(element, "", ({
                                                                                                      leftPart,
                                                                                                      selectedPart,
                                                                                                      rightPart
                                                                                                  }) => {
                                                let nextContent = leftPart + "*" + selectedPart + "*" + rightPart;
                                                updateContent(nextContent);

                                                contentChangeUndoStackHandler(nextContent);
                                            });
                                        }}>斜体</Button>
                                    </Tooltip>
                                    <Button type="link" onClick={(event) => {
                                        // @ts-ignore
                                        let element: HTMLTextAreaElement = document.getElementById(editor_text_area);
                                        InputElementHelper.appendTextToTextArea(element, "", ({
                                                                                                  leftPart,
                                                                                                  selectedPart,
                                                                                                  rightPart
                                                                                              }) => {
                                            let nextContent = leftPart + "~" + selectedPart + "~" + rightPart;
                                            updateContent(nextContent);

                                            contentChangeUndoStackHandler(nextContent);
                                        });
                                    }}>删除线</Button>
                                    <Button type="link" onClick={(event) => {
                                        // @ts-ignore
                                        let element: HTMLTextAreaElement = document.getElementById(editor_text_area);
                                        InputElementHelper.appendTextToTextArea(element, md_template_table, ({
                                                                                                                 appendTarget,
                                                                                                                 leftPart,
                                                                                                                 rightPart
                                                                                                             }) => {
                                            let nextContent = leftPart + appendTarget + rightPart;
                                            updateContent(nextContent);

                                            contentChangeUndoStackHandler(nextContent);
                                        });
                                    }}>表格</Button>
                                    <Button type="link" onClick={(event) => {
                                        // @ts-ignore
                                        let element: HTMLTextAreaElement = document.getElementById(editor_text_area);
                                        InputElementHelper.appendTextToTextArea(element, md_template_code, ({
                                                                                                                appendTarget,
                                                                                                                leftPart,
                                                                                                                rightPart
                                                                                                            }) => {
                                            let nextContent = leftPart + appendTarget + rightPart;
                                            updateContent(nextContent);

                                            contentChangeUndoStackHandler(nextContent);
                                        });
                                    }}>代码块</Button>
                                    <EditorHyperlinkModal/>
                                    <EditorImageModal/>
                                    <EditorBilibiliShareModal/>
                                    <Button type="link" onClick={(event) => {
                                        // @ts-ignore
                                        let element: HTMLTextAreaElement = document.getElementById(editor_text_area);
                                        InputElementHelper.appendTextToTextArea(element, md_template_scheduled_tasks, ({
                                                                                                                           appendTarget,
                                                                                                                           leftPart,
                                                                                                                           rightPart
                                                                                                                       }) => {
                                            let nextContent = leftPart + appendTarget + rightPart;
                                            updateContent(nextContent);

                                            contentChangeUndoStackHandler(nextContent);
                                        });
                                    }}>计划任务</Button>
                                    <Button type="link" onClick={() => {
                                        let nextContent = localStorage.getItem(key_draft);

                                        if (nextContent != null) {
                                            updateContent(nextContent);
                                            contentChangeUndoStackHandler(nextContent);
                                            message.info("加载草稿完成")
                                        } else {
                                            message.warning("未找到可用草稿")
                                        }
                                    }}>加载草稿</Button>
                                </Space>
                            </Row>
                    },
                    {
                        key: '2',
                        label: 'Html 标签',
                        children:
                            <Row gutter={[8, 8]}>
                                <Space size={"small"}>
                                    <Button type="link" onClick={(event) => {
                                        // @ts-ignore
                                        let element: HTMLTextAreaElement = document.getElementById(editor_text_area);
                                        InputElementHelper.appendTextToTextArea(element, "", ({
                                                                                                  leftPart,
                                                                                                  selectedPart,
                                                                                                  rightPart
                                                                                              }) => {
                                            let nextContent = leftPart + "<u>" + selectedPart + "</u>" + rightPart;
                                            updateContent(nextContent);

                                            contentChangeUndoStackHandler(nextContent);
                                        });
                                    }}>下划线</Button>
                                    <Button type="link" onClick={(event) => {
                                        // @ts-ignore
                                        let element: HTMLTextAreaElement = document.getElementById(editor_text_area);
                                        InputElementHelper.appendTextToTextArea(element, "", ({
                                                                                                  leftPart,
                                                                                                  selectedPart,
                                                                                                  rightPart
                                                                                              }) => {
                                            let nextContent = leftPart + "<sup>" + selectedPart + "</sup>" + rightPart;
                                            updateContent(nextContent);

                                            contentChangeUndoStackHandler(nextContent);
                                        });
                                    }}>上标</Button>
                                    <Button type="link" onClick={(event) => {
                                        // @ts-ignore
                                        let element: HTMLTextAreaElement = document.getElementById(editor_text_area);
                                        InputElementHelper.appendTextToTextArea(element, "", ({
                                                                                                  leftPart,
                                                                                                  selectedPart,
                                                                                                  rightPart
                                                                                              }) => {
                                            let nextContent = leftPart + "<sub>" + selectedPart + "</sub>" + rightPart;
                                            updateContent(nextContent);

                                            contentChangeUndoStackHandler(nextContent);
                                        });
                                    }}>下标</Button>
                                    <Button type="link" onClick={(event) => {
                                        // @ts-ignore
                                        let element: HTMLTextAreaElement = document.getElementById(editor_text_area);
                                        InputElementHelper.appendTextToTextArea(element, "", ({
                                                                                                  leftPart,
                                                                                                  selectedPart,
                                                                                                  rightPart
                                                                                              }) => {
                                            let nextContent = leftPart + md_template_summary + rightPart;
                                            updateContent(nextContent);

                                            contentChangeUndoStackHandler(nextContent);
                                        });
                                    }}>摘要</Button>
                                    <Button type="link" onClick={(event) => {
                                        // @ts-ignore
                                        let element: HTMLTextAreaElement = document.getElementById(editor_text_area);
                                        InputElementHelper.appendTextToTextArea(element, "", ({
                                                                                                  leftPart,
                                                                                                  selectedPart,
                                                                                                  rightPart
                                                                                              }) => {
                                            let nextContent = leftPart + md_template_acronym + rightPart;
                                            updateContent(nextContent);

                                            contentChangeUndoStackHandler(nextContent);
                                        });
                                    }}>缩略语</Button>
                                </Space>
                            </Row>
                    },
                    {
                        key: '3',
                        label: '　　其他　　',
                        children:
                            <Row gutter={[8, 8]}>
                                <Space size={"small"}>
                                    <Upload showUploadList={true} multiple={true} onChange={(info) => {
                                        console.log(info.file);
                                    }}>
                                        <Button type="link" icon={<UploadOutlined/>}>上传文件</Button>
                                    </Upload>
                                </Space>
                            </Row>
                    },
                ]} onChange={onChange}/>
            )}
        </EditorContext.Consumer>
    )
}

export default EditorMenu;