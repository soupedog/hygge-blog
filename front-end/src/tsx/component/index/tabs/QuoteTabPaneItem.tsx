import React from 'react';
import {Badge, List, Space, Tooltip} from "antd";
import {QuoteDto} from "../../../rest/ApiClient";
import {FormOutlined} from "@ant-design/icons";
import {class_md_preview} from "../../properties/ElementNameContainer";
import remarkGfm from "remark-gfm";
import remarkMath from "remark-math";
import rehypeKatex from "rehype-katex";
import rehypeSlug from "rehype-slug";
import rehypeRaw from "rehype-raw";
import ReactMarkdown from "react-markdown";
import {PropertiesHelper, UrlHelper} from "../../../util/UtilContainer";

function QuoteTabPaneItem({isAuthor, quote}: { isAuthor: Boolean, quote: QuoteDto }) {
    let quoteOrder = quote.orderVal;
    if (quoteOrder != null && quoteOrder > 0) {
        return (
            <Badge.Ribbon text="顶置" color="red">
                {renderCore(isAuthor, quote)}
            </Badge.Ribbon>
        );
    } else {
        return renderCore(isAuthor, quote);
    }
}

function renderCore(isAuthor: Boolean, quote: QuoteDto) {
    return (
        <List.Item
            extra={PropertiesHelper.isStringNotEmpty(quote.imageSrc) ?
                <img
                    width={272}
                    alt="logo"
                    src={quote.imageSrc}
                /> : null
            }
        >
            <List.Item.Meta
                title={
                    quote.source == null ? null :
                        <>
                            <Tooltip placement="right" title={"可能的出处"}>
                                <span style={{fontSize: "24px", fontWeight: "bold"}}>{quote.source}</span>
                            </Tooltip>
                            {
                                isAuthor ? <EditIcon icon={FormOutlined} text={"编辑"}
                                                     quoteId={quote.quoteId}/> : null
                            }
                        </>
                }
                description={
                    quote.portal == null ? null :
                        <>
                                <span
                                    style={{
                                        fontSize: "14px",
                                        color: "#0039f6",
                                        fontWeight: "bold"
                                    }}>&emsp;传送门:&emsp;</span>
                            <a href={quote.portal}
                               target="_blank">{quote.portal}</a>
                        </>
                }
            />
            <div className={".md-preview"} style={{
                color: "#9a0707",
            }}>
                <ReactMarkdown className={class_md_preview}
                               children={quote.content}
                               remarkPlugins={[remarkGfm, remarkMath]}
                               rehypePlugins={[rehypeKatex, rehypeSlug, rehypeRaw]}
                />
            </div>
        </List.Item>
    );
}

const EditIcon = ({icon, text, quoteId}: { icon: React.FC; text: string, quoteId: number }) => (
    <Space className={"pointer"} onClick={() => {
        UrlHelper.openNewPage({inNewTab: false, path: "editor/quote/" + quoteId})
    }} style={{
        float: "right",
        marginRight: "20px",
        fontSize: "14px"
    }}>
        {React.createElement(icon)}
        {text}
    </Space>
);

export default QuoteTabPaneItem;