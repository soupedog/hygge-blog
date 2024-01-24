import React, {useState} from 'react';
import {List} from "antd";
import {ArticleSummaryResponse, UserService} from "../../../rest/ApiClient";
import {UrlHelper} from "../../../util/UtilContainer";
import {IndexContext} from '../../../page/Index';
import ArticleSummaryTabPaneItem from "./ArticleSummaryTabPaneItem";
import {ArticleSummaryOrderType} from "../../properties/GlobalEnum";

function ArticleSummaryTabPane({orderType, articleSummaryInfo, onPageChange}: {
    orderType: ArticleSummaryOrderType,
    articleSummaryInfo: ArticleSummaryResponse,
    onPageChange: Function
}) {
    const [currentPageSize, updateCurrentPageSize] = useState(5);

    return (
        <IndexContext.Consumer>
            {({currentTopicId,currentCategoryId, updateArticleSummaryInfo}) => (
                <List
                    itemLayout="vertical"
                    size="large"
                    pagination={{
                        onChange: (page, pageSize) => {
                            onPageChange(currentTopicId!,currentCategoryId!, page, pageSize);

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