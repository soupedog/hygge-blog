import * as React from "react"
import {LogHelper, PropertiesHelper} from '../../../../utils/UtilContainer';
import {List} from 'antd';
import {ArticleSummaryInfo, HomePageService} from "../../../../rest/ApiClient";
import {ArticleOverviewViewItem} from "./ArticleOverviewViewItem";

// 描述该组件 props 数据类型
export interface ArticleOverviewContainerProps {
    tid: string,
    isMaintainer: boolean
}

// 描述该组件 states 数据类型
export interface ArticleOverviewContainerStatus {
    totalCount: number;
    currentPage: number;
    pageSize: number;
    articleSummaryList: ArticleSummaryInfo[];
}

export class ArticleOverviewContainer extends React.Component<ArticleOverviewContainerProps, ArticleOverviewContainerStatus> {
    constructor(props: ArticleOverviewContainerProps) {
        super(props);
        this.state = {
            totalCount: 0,
            currentPage: 1,
            pageSize: 1,
            articleSummaryList: []
        };
        LogHelper.info({className: "ArticleOverviewContainer", msg: "初始化成功"});
    }

    render() {
        let _react = this;
        return (
            <List
                itemLayout="vertical"
                size="large"
                pagination={{
                    onChange: (page, pageSize) => {
                        _react.fetchArticleInfo(page, pageSize);
                    },
                    showSizeChanger: true,
                    current: _react.state.currentPage,
                    pageSize: _react.state.pageSize,
                    total: _react.state.totalCount
                }}
                dataSource={_react.state.articleSummaryList}
                renderItem={item => (
                    <ArticleOverviewViewItem topicQuery={true} key={item.aid} isMaintainer={this.props.isMaintainer}
                                             currentArticle={item}/>
                )}
            />
        );
    }

    componentDidMount() {
        this.fetchArticleInfo(this.state.currentPage, this.state.pageSize);
    }

    private fetchArticleInfo(currentPage: number, pageSize: number) {
        let _react = this;

        let currentTid = _react.props.tid;
        if (PropertiesHelper.isStringNotEmpty(currentTid) && currentTid.length > 0) {
            HomePageService.fetchArticleSummaryByTid(_react.props.tid, currentPage, pageSize, (data) => {
                _react.setState({
                    articleSummaryList: data?.main!.articleSummaryList!,
                    totalCount: data?.main?.totalCount!,
                    currentPage: currentPage,
                    pageSize: pageSize
                });
            });
        }
    }
}
