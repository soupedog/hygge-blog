import ReactMarkdown from "react-markdown";
import remarkGfm from 'remark-gfm' // 渲染表格、checkBox 等组件
import rehypeRaw from 'rehype-raw' // 允许原生 html 渲染
import rehypeSlug from 'rehype-slug' // 标题标签标记描点
import rehypeHighlight from 'rehype-highlight' // 代码高亮标记
import remarkMath from 'remark-math' // 数学公式支持
import rehypeKatex from 'rehype-katex' // 数学公式支持
import bash from 'highlight.js/lib/languages/bash';
import shell from 'highlight.js/lib/languages/shell'
import dockerfile from 'highlight.js/lib/languages/dockerfile';
import nginx from 'highlight.js/lib/languages/nginx';
import javascript from 'highlight.js/lib/languages/javascript';
import typescript from 'highlight.js/lib/languages/typescript';
import java from 'highlight.js/lib/languages/java';
import python from 'highlight.js/lib/languages/python';
import sql from 'highlight.js/lib/languages/sql';
import properties from 'highlight.js/lib/languages/properties';
import json from 'highlight.js/lib/languages/json';
import xml from 'highlight.js/lib/languages/xml';
import yaml from 'highlight.js/lib/languages/yaml';
import {Col, message, Row, Tree} from "antd";
import React from "react";
import TextArea from "antd/es/input/TextArea";
import {class_md_preview, editor_text_area} from "../properties/ElementNameContainer";
import {key_draft} from "./properties/MarkDownStaticValue";
import {DownOutlined} from '@ant-design/icons';
import {TreeProps} from "antd/es/tree/Tree";
import InputElementHelper from "./util/InputElementHelper";
import { ArticleEditorContext } from "../../page/ArticleEditor";

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

function EditorView() {
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
        <ArticleEditorContext.Consumer>
            {({content, updateContent, tocEnable, tocTree}) => (
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
                        <TextArea id={editor_text_area} rows={27}
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
                                          let element: HTMLTextAreaElement = document.getElementById(editor_text_area);

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
                        <ReactMarkdown className={class_md_preview}
                                       children={content}
                                       remarkPlugins={[remarkGfm, remarkMath]}
                                       rehypePlugins={[rehypeKatex, rehypeSlug, rehypeRaw, [rehypeHighlight, {
                                           detect: true,// 没有 language 属性的代码尝试自动解析语言类型
                                           ignoreMissing: true, // 出现故障不抛出异常打断页面渲染
                                           languages: {// 默认会装载部分语言，但手动更完整和准确
                                               bash,
                                               shell,
                                               dockerfile,
                                               nginx,
                                               javascript,
                                               typescript,
                                               java,
                                               python,
                                               sql,
                                               properties,
                                               json,
                                               xml,
                                               yaml
                                           }
                                       }]]}
                        />
                    </Col>
                </Row>
            )}
        </ArticleEditorContext.Consumer>
    );
}

export default EditorView;