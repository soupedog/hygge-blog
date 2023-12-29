import React, {useState} from 'react';
import {List} from "antd";
import {HomePageService, QuoteResponse, UserService} from "../../../rest/ApiClient";
import QuoteTabPaneItem from "./QuoteTabPaneItem";
import {IndexContext} from '../../page/Index';

function QuoteTabPane({quoteInfo}: { quoteInfo: QuoteResponse }) {
    const [currentPageSize, updateCurrentPageSize] = useState(5);

    return (
        <IndexContext.Consumer>
            {({updateQuoteInfo}) => (
                <List
                    itemLayout="vertical"
                    size="large"
                    pagination={{
                        onChange: (page, pageSize) => {
                            HomePageService.fetchQuote(page, pageSize, (data) => {
                                updateQuoteInfo(data?.main);
                            });

                            if (pageSize != currentPageSize) {
                                updateCurrentPageSize(pageSize);
                            }
                        },
                        showSizeChanger: true,
                        total: quoteInfo.totalCount,
                        pageSize: currentPageSize,

                    }}
                    dataSource={quoteInfo.quoteList}
                    renderItem={(item) => (
                        <QuoteTabPaneItem isAuthor={item.uid == UserService.getCurrentUser()?.uid} quote={item}/>
                    )}
                />
            )}
        </IndexContext.Consumer>

    );
}

export default QuoteTabPane;