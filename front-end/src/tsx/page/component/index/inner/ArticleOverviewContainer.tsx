import * as React from "react"
import {LogHelper, PropertiesHelper, TimeHelper, UrlHelper} from '../../../../utils/UtilContainer';

import {DashboardTwoTone, EditTwoTone, EyeTwoTone} from '@ant-design/icons';
import {List, Space} from 'antd';
import {ArticleSummaryInfo, HomePageService} from "../../../../rest/ApiClient";

// 描述该组件 props 数据类型
export interface ArticleOverviewContainerProps {
    tid: string
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
                        console.log(page + "  " + pageSize);
                    },
                    showSizeChanger: true,
                    current: _react.state.currentPage,
                    pageSize: _react.state.pageSize,
                    total: _react.state.totalCount
                }}
                dataSource={_react.state.articleSummaryList}
                renderItem={item => (
                    <List.Item
                        key={item.title}
                        actions={[
                            <IconText icon={EditTwoTone} text={"字数 " + item.wordCount} key={"word_count_" + item.aid}/>,
                            <IconText icon={EyeTwoTone} text={"浏览量 " + item.pageViews} key={"word_count_" + item.aid}/>,
                            <IconText icon={DashboardTwoTone}
                                      text={"创建时间 " + TimeHelper.formatTimeStampToString(item.createTs)}
                                      key={"create_ts_" + item.aid}/>,
                        ]}
                        extra={
                            <img
                                width={272}
                                alt="logo"
                                src={item.imageSrc}
                            />
                        }
                        style={{
                            // backgroundColor: "red"
                        }}
                    >
                        <List.Item.Meta
                            title={<a style={{fontSize: "32px", fontWeight: 900, lineHeight: "40px"}}
                                      href={UrlHelper.getBaseUrl() + "#/browser/" + item.aid}
                                      target="_blank">{item.title}</a>}
                            description={_react.getCategoryInfo(item)}
                        />
                        <div style={{textIndent: "2em", fontSize: "14px", lineHeight: "24px"}}>
                            {item.summary}
                        </div>
                    </List.Item>
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
        if (PropertiesHelper.isStringNotNull(currentTid) && currentTid.length > 0) {
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

    getCategoryInfo(articleSummary: ArticleSummaryInfo): string {
        let result = articleSummary.categoryTreeInfo.topicInfo.topicName;

        articleSummary.categoryTreeInfo.categoryList.forEach((item) => {
            result = result + " / " + item.categoryName
        });
        return result;
    }
}

const IconText = ({icon, text}: { icon: React.FC; text: string }) => (
    <Space>
        {React.createElement(icon)}
        {text}
    </Space>
);