import React from 'react';
import {ArticleSummaryResponse, QuoteResponse} from "../../../../rest/ApiClient";
import {ArticleSummaryOrderType, IndexSearchType} from "../../properties/GlobalEnum";

function SearchResultTabPane({searchType, orderType, articleSummaryInfo, quoteInfo}: {
    searchType: IndexSearchType,
    orderType: ArticleSummaryOrderType,
    articleSummaryInfo: ArticleSummaryResponse,
    quoteInfo: QuoteResponse
}) {
    switch (searchType) {
        case IndexSearchType.ARTICLE:
            return (
                <>
                </>
            );
        case IndexSearchType.QUOTE:
            return (
                <>
                </>
            );
        default :
            return (
                <>
                </>
            );
    }
}

export default SearchResultTabPane;