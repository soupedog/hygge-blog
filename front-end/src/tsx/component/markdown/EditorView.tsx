import React from "react";

import {Col, message, Row, Tree} from "antd";
import TextArea from "antd/es/input/TextArea";
import {id_editor_text_area} from "../properties/ElementNameContainer";
import {allowAll, editor_id_for_editor, key_draft} from "./properties/MarkDownStaticValue";
import {DownOutlined} from '@ant-design/icons';
import {TreeProps} from "antd/es/tree/Tree";
import InputElementHelper from "./util/InputElementHelper";
import {AntdTreeNodeInfo} from "./util/MdHelper";
import {MdPreview} from "md-editor-rt";

const stackMaxSize = 20;
const undoStack: string[] = new Array<string>(); // 用于存储撤销历史记录
// 初始为空
undoStack.push("");
const redoStack: string[] = new Array<string>(); // 用于存储重做历史记录

// 阻断事件向上冒泡
function stopEvent(event: any) {
    event.preventDefault();
}

export function contentChangeUndoStackHandler(content: string) {
    undoStack.push(content);

    // 栈容量限制
    if (undoStack.length > stackMaxSize) {
        undoStack.shift();
        // 清空
        redoStack.length = 0;
    }
}

export function contentChangeTextAreaPostHandler(element: HTMLTextAreaElement, cursorIndex: number) {
    // react 更新和 dom 操作间是异步的，这里用延时不太靠谱地指定 dom 操作在 react 更新后执行，
    setTimeout(function () {
        element.setSelectionRange(cursorIndex, cursorIndex)
    }, 50);
}

export interface EditorViewProps {
    content: string;
    updateContent: Function;
    tocEnable: boolean;
    tocTree: AntdTreeNodeInfo[];
}

function EditorView({content, updateContent, tocEnable, tocTree}: EditorViewProps) {
    const [messageApi, contextHolder] = message.useMessage();

    // 目录选中自动跳转函数
    const onSelect: TreeProps['onSelect'] = (selectedKeys, info) => {
        // @ts-ignore
        let element = document.getElementById(info.node.value);

        if (element != null) {
            // 滚动到锚点元素的顶部
            element.scrollIntoView({behavior: "smooth", block: "start", inline: "nearest"});
        } else {
            message.warning("未找到对应跳转锚点")
        }
    };

    return (
        <Row gutter={[8, 8]} style={{
            marginTop: "8px",
            paddingBottom: "8px"
        }}>
            {tocEnable ?
                <Col span={4}>
                    <Tree
                        showLine
                        switcherIcon={<DownOutlined/>}
                        onSelect={onSelect}
                        treeData={tocTree as any}
                    />
                </Col>
                : null}
            <Col span={tocEnable ? 8 : 12} style={{maxHeight: "600px"}}>
                {contextHolder}
                <TextArea id={id_editor_text_area} rows={27}
                          placeholder="这里是 markdown 编辑器写作区，请开始您的创作吧！
                                        Ctrl + B 加粗
                                        Ctrl + D 删除当前行
                                        Ctrl + I 斜体
                                        Ctrl + S 保存草稿
                                        Ctrl + Y 回退
                                        Ctrl + Z 撤销
                                        "
                          value={content}
                          onChange={event => {
                              contentChangeUndoStackHandler(event.target.value);

                              updateContent(event.target.value);
                          }}
                          onKeyDown={(event) => {
                              // 如果是 ctrl 组合键
                              if (event.ctrlKey) {
                                  // @ts-ignore
                                  let element: HTMLTextAreaElement = document.getElementById(id_editor_text_area);

                                  switch (event.key) {
                                      case "s":
                                          // 保存
                                          stopEvent(event);

                                          localStorage.setItem(key_draft, element.textContent!);

                                          messageApi.success("保存草稿成功");
                                          break;
                                      case "d":
                                          // 删除行
                                          stopEvent(event);

                                          InputElementHelper.removeSelectedLine(element, ({
                                                                                              leftPart,
                                                                                              rightPart
                                                                                          }) => {
                                              let nextContent = leftPart + rightPart;
                                              updateContent(nextContent);

                                              contentChangeUndoStackHandler(nextContent);
                                              contentChangeTextAreaPostHandler(element, leftPart.length);
                                          });
                                          break;
                                      case "b":
                                          // 加粗
                                          stopEvent(event);
                                          InputElementHelper.appendTextToTextArea(element, "", ({
                                                                                                    leftPart,
                                                                                                    selectedPart,
                                                                                                    rightPart
                                                                                                }) => {
                                              let nextContent = leftPart + "**" + selectedPart + "**" + rightPart;
                                              updateContent(nextContent);

                                              contentChangeUndoStackHandler(nextContent);
                                          });
                                          break;
                                      case "i":
                                          // 斜体
                                          stopEvent(event);
                                          InputElementHelper.appendTextToTextArea(element, "", ({
                                                                                                    leftPart,
                                                                                                    selectedPart,
                                                                                                    rightPart
                                                                                                }) => {
                                              let nextContent = leftPart + "*" + selectedPart + "*" + rightPart;
                                              updateContent(nextContent);

                                              contentChangeUndoStackHandler(nextContent);
                                          });
                                          break;
                                      case "z":
                                          // 撤销
                                          stopEvent(event);

                                          if (undoStack.length > 0) {
                                              let nextContent = undoStack.pop();

                                              if (nextContent == null) {
                                                  break;
                                              }

                                              redoStack.push(nextContent);

                                              if (nextContent == element.textContent!) {
                                                  nextContent = undoStack.pop();
                                                  redoStack.push(nextContent!);
                                              }
                                              updateContent(nextContent);
                                          }

                                          break;
                                      case "y":
                                          // 回退
                                          stopEvent(event);

                                          if (redoStack.length > 0) {
                                              let nextContent = redoStack.pop();
                                              if (nextContent == null) {
                                                  break;
                                              }
                                              undoStack.push(nextContent);

                                              if (nextContent == element.textContent) {
                                                  nextContent = redoStack.pop();
                                                  undoStack.push(nextContent!);
                                              }
                                              updateContent(nextContent);
                                          }

                                          break;
                                      default:
                                  }
                              }
                          }}
                    // 不允许文本域调整大小
                          style={{resize: "none"}}
                />
            </Col>
            <Col span={12} style={{maxHeight: "600px", overflowY: "scroll"}}>
                <MdPreview editorId={editor_id_for_editor} modelValue={content} sanitize={allowAll}/>
            </Col>
            <Col span={24}>
            </Col>
        </Row>
    );
}

export default EditorView;