import * as React from "react"
import {LogHelper} from '../../../../utils/UtilContainer';
import {List} from 'antd';
import {HomePageService, QuoteDto} from "../../../../rest/ApiClient";
import {QuoteViewItem} from "./QuoteViewItem";


// 描述该组件 props 数据类型
export interface QuoteContainerProps {
    isMaintainer: boolean
}

// 描述该组件 states 数据类型
export interface QuoteContainerStatus {
    totalCount: number;
    currentPage: number;
    pageSize: number;
    quoteList: QuoteDto[];
}

export class QuoteContainer extends React.Component<QuoteContainerProps, QuoteContainerStatus> {
    constructor(props: QuoteContainerProps) {
        super(props);
        this.state = {
            totalCount: 0,
            currentPage: 1,
            pageSize: 5,
            quoteList: []
        };
        LogHelper.info({className: "QuoteContainer", msg: "初始化成功"});
    }

    render() {
        let _react = this;
        return (
            <List
                itemLayout="vertical"
                size="large"
                pagination={{
                    onChange: (page, pageSize) => {
                        _react.fetchQuoteList(page, pageSize);
                    },
                    showSizeChanger: true,
                    current: _react.state.currentPage,
                    pageSize: _react.state.pageSize,
                    total: _react.state.totalCount
                }}
                dataSource={_react.state.quoteList}
                renderItem={(item) => (
                    <QuoteViewItem isMaintainer={this.props.isMaintainer} key={item.quoteId} currentQuote={item}/>
                )}
            />
        );
    }

    componentDidMount() {
        this.fetchQuoteList(this.state.currentPage, this.state.pageSize);
    }

    fetchQuoteList(currentPage: number, pageSize: number) {
        let _react = this;

        HomePageService.fetchQuote(currentPage, pageSize, (data) => {
            _react.setState({
                quoteList: data?.main!.quoteList!,
                totalCount: data?.main?.totalCount!,
                currentPage: currentPage,
                pageSize: pageSize
            });
        });
    }
}