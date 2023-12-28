import React, {useState} from 'react';
import {List, Space} from "antd";
import ArticleSummaryItem, {ArticleSummaryOrderType} from "./ArticleSummaryItem";
import {ArticleSummaryResponse, HomePageService, UserService} from "../../../rest/ApiClient";
import {UrlHelper} from "../../util/UtilContainer";
import {IndexContext} from '../../page/Index';

const IconText = ({icon, text}: { icon: React.FC; text: string }) => (
    <Space>
        {React.createElement(icon)}
        {text}
    </Space>
);

function ArticleSummaryContainer({orderType, articleSummaryInfo}: {
    orderType: ArticleSummaryOrderType,
    articleSummaryInfo: ArticleSummaryResponse
}) {
    const [currentPage, updateCurrentPage] = useState(1);
    const [currentPageSize, updateCurrentPageSize] = useState(5);

    return (
        <IndexContext.Consumer>
            {({currentTopicId, updateArticleSummaryInfo}) => (
                <List
                    itemLayout="vertical"
                    size="large"
                    pagination={{
                        onChange: (page, pageSize) => {
                            HomePageService.fetchArticleSummaryByTid(currentTopicId!, page, pageSize, (data) => {
                                updateArticleSummaryInfo(data?.main);
                            });

                            if (pageSize != currentPageSize) {
                                updateCurrentPageSize(pageSize);
                            }
                        },
                        showSizeChanger: true,
                        total: articleSummaryInfo.totalCount,
                        pageSize: currentPageSize,

                    }}
                    dataSource={articleSummaryInfo.articleSummaryList}
                    renderItem={(item) => (
                        <ArticleSummaryItem
                            //TODO isAuthor 语义判断不准确，有待调整
                            isAuthor={UserService.getCurrentUser() != null}
                            articleSummary={item}
                            orderType={orderType}
                            secretKey={UrlHelper.getQueryString("secretKey")}
                        />
                    )}
                />
            )}
        </IndexContext.Consumer>

    );
}

export default ArticleSummaryContainer;