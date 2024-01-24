import React, {useState} from 'react';
import {List} from "antd";
import {QuoteResponse, UserService} from "../../../rest/ApiClient";
import QuoteTabPaneItem from "./QuoteTabPaneItem";
import {IndexContext} from "../../../page/Index";

function QuoteTabPane({quoteInfo, onPageChange}: { quoteInfo: QuoteResponse, onPageChange: Function }) {
    const [currentPageSize, updateCurrentPageSize] = useState(5);

    return (
        <IndexContext.Consumer>
            {({updateQuoteInfo}) => (
                <List
                    itemLayout="vertical"
                    size="large"
                    pagination={{
                        onChange: (page, pageSize) => {
                            onPageChange(page, pageSize);

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