import * as React from "react"
import {LogHelper, PropertiesHelper, UrlHelper} from '../../../../utils/UtilContainer';
import {List, Space, Tooltip} from 'antd';
import {QuoteDto} from "../../../../rest/ApiClient";
import Vditor from "vditor";
import {EditTwoTone} from "@ant-design/icons";

// 描述该组件 props 数据类型
export interface QuoteViewItemProps {
    currentQuote: QuoteDto,
    isMaintainer: boolean
}

// 描述该组件 states 数据类型
export interface QuoteViewItemStatus {
}

export class QuoteViewItem extends React.Component<QuoteViewItemProps, QuoteViewItemStatus> {
    constructor(props: QuoteViewItemProps) {
        super(props);
        this.state = {};
        LogHelper.info({className: "QuoteViewItem", msg: "初始化成功"});
    }

    render() {
        let _react = this;
        return (
            <List.Item
                key={this.props.currentQuote.quoteId}
                actions={[
                    this.props.isMaintainer ? <IconText icon={EditTwoTone} text={"编辑"} quoteId={this.props.currentQuote.quoteId}
                                                        key={"edit_" + this.props.currentQuote.quoteId}/> : null
                ]}
                extra={PropertiesHelper.isStringNotEmpty(this.props.currentQuote.imageSrc) ?
                    <img
                        width={272}
                        alt="logo"
                        src={this.props.currentQuote.imageSrc}
                    /> : null
                }
            >
                <List.Item.Meta
                    title={
                        this.props.currentQuote.source == null ? null :
                            <Tooltip placement="bottom" title={"可能的出处"}>
                                {this.props.currentQuote.source}
                            </Tooltip>
                    }
                    description={
                        this.props.currentQuote.portal == null ? null :
                            <>
                                <span>传送门:&emsp;</span>
                                <a href={this.props.currentQuote.portal}
                                   target="_blank">{this.props.currentQuote.portal}</a>
                            </>
                    }
                />
                <div id={"quote_content_" + this.props.currentQuote.quoteId}>
                </div>
            </List.Item>
        );
    }

    componentDidMount() {
        Vditor.preview(document.getElementById("quote_content_" + this.props.currentQuote.quoteId) as HTMLDivElement,
            this.props.currentQuote.content,
            {
                mode: "dark",
                // cdn: "https://www.xavierwang.cn/static/npm/vditor@3.8.5",
                anchor: 0,
                hljs: {
                    style: "native",
                    lineNumber: true
                },
                markdown: {
                    sanitize: false,
                    toc: true
                },
                after: () => {
                    // 清除代码高度限制
                }
            });
    }
}

const IconText = ({icon, text, quoteId}: { icon: React.FC; text: string, quoteId: number }) => (
    <Space className={"pointer"} onClick={() => {
        UrlHelper.openNewPage({inNewTab: false, path: "#/editor/quote/" + quoteId})
    }}>
        {React.createElement(icon)}
        {text}
    </Space>
);