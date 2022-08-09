import * as React from "react"
import {LogHelper} from '../../../../utils/UtilContainer';
import {List} from 'antd';
import {HomePageService, QuoteDto} from "../../../../rest/ApiClient";
import {QuoteViewItem} from "./QuoteViewItem";


// 描述该组件 props 数据类型
export interface SearchResultProps {
    isMaintainer: boolean
}

// 描述该组件 states 数据类型
export interface SearchResultStatus {
    totalCount: number;
    currentPage: number;
    pageSize: number;
    quoteList: QuoteDto[];
}

export class SearchResultContainer extends React.Component<SearchResultProps, SearchResultStatus> {
    constructor(props: SearchResultProps) {
        super(props);
        this.state = {
            totalCount: 0,
            currentPage: 1,
            pageSize: 5,
            quoteList: []
        };
        LogHelper.info({className: "SearchResult", msg: "初始化成功"});
    }

    render() {
        let _react = this;
        return (
            <List
                itemLayout="vertical"
                size="large"
                pagination={{
                    onChange: (page, pageSize) => {
                    },
                    showSizeChanger: true,
                    current: _react.state.currentPage,
                    pageSize: _react.state.pageSize,
                    total: _react.state.totalCount
                }}
                dataSource={[]}
                renderItem={(item) => (
                    <QuoteViewItem isMaintainer={this.props.isMaintainer} key={"item.quoteId"} currentQuote={item}/>
                )}
            />
        );
    }

    componentDidMount() {
    }
}