import * as React from "react"
import {LogHelper} from '../../../../utils/UtilContainer';
import {List} from 'antd';
import {QuoteViewItem} from "./QuoteViewItem";
import {ArticleOverviewViewItem} from "./ArticleOverviewViewItem";
import {IndexContainerState, SearchType} from "../../../IndexContainer";
import {IndexContainerContext} from "../../../context/HyggeContext";


// 描述该组件 props 数据类型
export interface SearchResultProps {
    isMaintainer: boolean,
}

// 描述该组件 states 数据类型
export interface SearchResultStatus {
    totalCount: number,
    currentPage: number,
    pageSize: number,
}

export class SearchResultContainer extends React.Component<SearchResultProps, SearchResultStatus> {
    constructor(props: SearchResultProps) {
        super(props);
        this.state = {
            totalCount: 0,
            currentPage: 1,
            pageSize: 5
        };
        LogHelper.info({className: "SearchResult", msg: "初始化成功"});
    }

    render() {
        let _react = this;
        return (
            <IndexContainerContext.Consumer>
                {(state: IndexContainerState) => (
                    <List
                        itemLayout="vertical"
                        size="large"
                        pagination={{
                            onChange: (page, pageSize) => {
                                state.fetchSearchViewInfo!(page, pageSize, state, state.currentCid, (data: any) => {
                                    _react.setState({
                                        totalCount: data.totalCount,
                                        currentPage: page,
                                        pageSize: pageSize
                                    });
                                });
                            },
                            showSizeChanger: true,
                            current: _react.state.currentPage,
                            pageSize: _react.state.pageSize,
                            total: state.searchResultInfoList?.totalCount
                        }}
                        dataSource={state.searchResultInfoList?.viewInfoList}
                        renderItem={(item) => (
                            state.searchType == SearchType.QUOTE ?
                                <QuoteViewItem isMaintainer={this.props.isMaintainer} key={item.quoteId}
                                               currentQuote={item}/> :
                                <ArticleOverviewViewItem isMaintainer={this.props.isMaintainer} key={item.aid}
                                                         topicQuery={false}
                                                         currentArticle={item}/>
                        )}
                    />
                )}
            </IndexContainerContext.Consumer>
        );
    }
}