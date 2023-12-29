import React, {useState} from 'react';
import {List} from "antd";
import ArticleSummaryTabPaneItem, {ArticleSummaryOrderType} from "./ArticleSummaryTabPaneItem";
import {ArticleSummaryResponse, HomePageService, UserService} from "../../../../rest/ApiClient";
import {UrlHelper} from "../../../util/UtilContainer";
import {IndexContext} from '../../../page/Index';

function ArticleSummaryTabPane({orderType, articleSummaryInfo}: {
    orderType: ArticleSummaryOrderType,
    articleSummaryInfo: ArticleSummaryResponse
}) {
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
                        <ArticleSummaryTabPaneItem
                            isAuthor={item.uid == UserService.getCurrentUser()?.uid}
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

export default ArticleSummaryTabPane;