import React, {useState} from 'react';
import {List} from "antd";
import {ArticleSummaryResponse, UserService} from "../../../rest/ApiClient";
import {UrlHelper} from "../../../util/UtilContainer";
import {IndexContext} from '../../../page/Index';
import ArticleSummaryTabPaneItem from "./ArticleSummaryTabPaneItem";
import {ArticleSummaryOrderType} from "../../properties/GlobalEnum";

export interface ArticleSummaryTabPaneProps {
    orderType: ArticleSummaryOrderType,
    articleSummaryInfo: ArticleSummaryResponse,
    onPageChange: Function
}

function ArticleSummaryTabPane({orderType, articleSummaryInfo, onPageChange}: ArticleSummaryTabPaneProps) {
    const [loading, setLoading] = useState(false);

    return (
        <IndexContext.Consumer>
            {({
                  currentTopicId,
                  currentCategoryId,
                  pageForSearch,
                  updatePageForSearch,
                  pageSizeForSearch,
                  updatePageSizeForSearch
              }) => (
                <List
                    itemLayout="vertical"
                    size="large"
                    loading={loading}
                    pagination={{
                        onChange: (page, pageSize) => {
                            let needRefresh = false;

                            if (page != pageForSearch) {
                                updatePageForSearch(page);
                                needRefresh = true;
                            } else if (pageSize != pageSizeForSearch) {
                                updatePageSizeForSearch(pageSize);
                                needRefresh = true;
                            }

                            if (needRefresh) {
                                onPageChange(currentTopicId, currentCategoryId, page, pageSize);
                            }
                        },
                        showSizeChanger: true,
                        total: articleSummaryInfo.totalCount,
                        current: pageForSearch,
                        pageSize: pageSizeForSearch,
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