import React from 'react';
import {ArticleSummaryResponse, HomePageService, QuoteResponse} from "../../../../rest/ApiClient";
import {ArticleSummaryOrderType, IndexSearchType} from "../../properties/GlobalEnum";
import ArticleSummaryTabPane from "./ArticleSummaryTabPane";
import {IndexContext} from '../../../page/Index';
import QuoteTabPane from "./QuoteTabPane";

function SearchResultTabPane({searchType, orderType, articleSummaryInfo, quoteInfo}: {
    searchType: IndexSearchType,
    orderType: ArticleSummaryOrderType,
    articleSummaryInfo: ArticleSummaryResponse,
    quoteInfo: QuoteResponse
}) {

    switch (searchType) {
        case IndexSearchType.ARTICLE:
            return (
                <IndexContext.Consumer>
                    {({updateArticleSummarySearchInfo}) => (
                        <ArticleSummaryTabPane orderType={orderType}
                                               articleSummaryInfo={articleSummaryInfo}
                                               onPageChange={(currentTopicId: string, currentCategoryId: string, page: number, pageSize: number) => {
                                                   HomePageService.fetchArticleSummaryByCid(currentCategoryId, page, pageSize, (data) => {
                                                       updateArticleSummarySearchInfo(data?.main);
                                                   });
                                               }}/>
                    )}
                </IndexContext.Consumer>
            );
        case IndexSearchType.QUOTE:
            return (
                <IndexContext.Consumer>
                    {({updateQuoteSearchInfo}) => (
                        <QuoteTabPane quoteInfo={quoteInfo} onPageChange={(page: number, pageSize: number) => {
                            // HomePageService.fetchQuote(page, pageSize, (data) => {
                            //     updateQuoteSearchInfo(data?.main);
                            // });
                        }}/>
                    )}
                </IndexContext.Consumer>
            );
        default :
            return (
                <>
                </>
            );
    }
}

export default SearchResultTabPane;