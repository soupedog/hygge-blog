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
import {AntdTreeNodeInfo, MdHelper} from "./util/MdHelper";
import InputElementHelper from "./util/InputElementHelper";
import {FileInfo, UserService} from "../../rest/ApiClient";
import {UrlHelper} from "../../util/UtilContainer";
import * as prettier from 'prettier';
import parserMarkdown from 'prettier/plugins/markdown';
// @ts-ignore
import {pangu} from 'pangu/browser';

export interface EditorMenuProps {
    updateContent: Function;
    tocEnable: boolean;
    updateTocEnable: Function;
    updateTocTree: Function;
}

function EditorMenu({updateContent, tocEnable, updateTocEnable, updateTocTree}: EditorMenuProps) {

    return (
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

                                let currentTOC = MdHelper.initTitleTree(
                                    {
                                        // @ts-ignore
                                        currentTOCArray: antdTreeNodeInfos,
                                        // @ts-ignore
                                        allTocNodeMap: map,
                                        // @ts-ignore
                                        errorCallback: null
                                    }
                                );

                                updateTocTree(currentTOC);

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
                                        let nextContent = leftPart + "_" + selectedPart + "_" + rightPart;
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
                                    let nextContent = leftPart + "~~" + selectedPart + "~~" + rightPart;
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
                            <Button type="link" onClick={() => {
                                // @ts-ignore
                                let element: HTMLTextAreaElement = document.getElementById(editor_text_area);
                                InputElementHelper.appendTextToTextArea(element, "", ({
                                                                                          leftPart,
                                                                                          selectedPart,
                                                                                          rightPart
                                                                                      }) => {
                                    if (selectedPart != null && selectedPart.length > 0) {
                                        // 你好Tom → 你好 Tom
                                        let nextContent = leftPart + pangu.spacingText(selectedPart) + rightPart;
                                        updateContent(nextContent);

                                        contentChangeUndoStackHandler(nextContent);
                                        message.info("已将中外文本追加必要间隔")
                                    } else {
                                        message.warning("未发现待处理的文本内容")
                                    }
                                });
                            }}>文本间隔</Button>
                            <Button type="link" onClick={() => {
                                // @ts-ignore
                                let element: HTMLTextAreaElement = document.getElementById(editor_text_area);
                                let currentContent: string | null = element.textContent;
                                if (currentContent != null) {
                                    //TODO 此处代码引发了 webpack --mode development 运行正常 而 production 运行报错
                                    prettier.format(currentContent, {
                                        parser: 'markdown',
                                        plugins: [parserMarkdown]
                                    }).then(nextContent => {
                                        updateContent(nextContent);
                                        contentChangeUndoStackHandler(nextContent);
                                        message.info("已将文本格式化标准形式")
                                    })
                                } else {
                                    message.warning("未发现待美化的文本内容")
                                }
                            }}>美化文本</Button>
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
                            <Upload
                                name={"files"}
                                action={UrlHelper.getBaseApiUrl() + "/main/file?type=ARTICLE"}
                                headers={UserService.getHeader({})}
                                showUploadList={true} multiple={true} onChange={(info) => {
                                if (info.file.status == "done") {
                                    let response = info.file.response;

                                    if (response.code == 200) {
                                        response.main.forEach((item: FileInfo) => {
                                            // @ts-ignore
                                            let element: HTMLTextAreaElement = document.getElementById(editor_text_area);
                                            InputElementHelper.appendTextToTextArea(element, "", ({
                                                                                                      leftPart,
                                                                                                      selectedPart,
                                                                                                      rightPart
                                                                                                  }) => {
                                                let nextContent = leftPart + "[" + item.name + "](" + UrlHelper.getBaseStaticSourceUrl() + item.src + ")\r\n\r\n" + rightPart;
                                                updateContent(nextContent);

                                                contentChangeUndoStackHandler(nextContent);
                                            });
                                        });

                                        message.success(`${info.file.name} 上传成功.`);
                                    } else {
                                        message.error(`${info.file.name} 上传失败.`);
                                        console.log(info.file.response);
                                    }
                                }
                            }}>
                                <Button type="link" icon={<UploadOutlined/>}>上传文件</Button>
                            </Upload>
                        </Space>
                    </Row>
            },
        ]}
        />
    )
}

export default EditorMenu;