import React from 'react';
import {Badge, Tabs, TabsProps} from "antd";
import {IndexContext} from '../../page/Index';
import {
    AnnouncementDto,
    ArticleSummaryResponse,
    HomePageService,
    QuoteResponse,
    TopicOverviewInfo
} from "../../rest/ApiClient";
import AnnouncementTabPane from "./tabs/AnnouncementTabPane";
import ArticleSummaryTabPane from "./tabs/ArticleSummaryTabPane";
import QuoteTabPane from "./tabs/QuoteTabPane";
import {ArticleSummaryOrderType, IndexSearchType} from "../properties/GlobalEnum";
import SearchResultTabPane from "./tabs/SearchResultTabPane";

function IndexMainView() {
    return (
        <IndexContext.Consumer>
            {({
                  updateCategoryFolded,
                  topicOverviewInfos,
                  updateCurrentTopicId,
                  articleSummaryInfo, updateArticleSummaryInfo,
                  indexSearchType,
                  quoteInfo, updateQuoteInfo,
                  articleSummarySearchOrderType,
                  articleSummarySearchInfo,
                  quoteSearchInfo,
                  announcementInfos
              }) => (
                <Tabs type="card" size={"large"}
                    // @ts-ignore
                      items={createTabs({
                          topicOverviewInfo: topicOverviewInfos,
                          articleSummaryInfo: articleSummaryInfo,
                          quoteInfo: quoteInfo,
                          indexSearchType: indexSearchType,
                          articleSummarySearchOrderType: articleSummarySearchOrderType,
                          articleSummarySearchInfo: articleSummarySearchInfo,
                          quoteSearchInfo: quoteSearchInfo,
                          announcementInfos: announcementInfos,
                          updateArticleSummaryInfo: updateArticleSummaryInfo,
                          updateQuoteInfo: updateQuoteInfo
                      })}
                      onChange={(key) => {
                          switch (key) {
                              case "句子收藏":
                              case "公告":
                                  updateCategoryFolded(true);
                                  break;
                              case "搜索结果":
                                  break;
                              default :
                                  updateCategoryFolded(false);
                                  updateCurrentTopicId(key)
                                  HomePageService.fetchArticleSummaryByTid(key, 1, 5, (data) => {
                                      updateArticleSummaryInfo(data?.main);
                                  });
                          }
                      }}>
                </Tabs>
            )}
        </IndexContext.Consumer>
    );
}

function createTabs({
                        topicOverviewInfo,
                        articleSummaryInfo, updateArticleSummaryInfo,
                        quoteInfo, updateQuoteInfo,
                        indexSearchType,
                        articleSummarySearchOrderType,
                        articleSummarySearchInfo,
                        quoteSearchInfo,
                        announcementInfos,
                    }: {
    topicOverviewInfo: TopicOverviewInfo[],
    articleSummaryInfo: ArticleSummaryResponse, updateArticleSummaryInfo: Function,
    quoteInfo: QuoteResponse, updateQuoteInfo: Function,
    indexSearchType: IndexSearchType,
    articleSummarySearchOrderType: ArticleSummaryOrderType,
    articleSummarySearchInfo: ArticleSummaryResponse,
    quoteSearchInfo: QuoteResponse,
    announcementInfos: AnnouncementDto[]
}): TabsProps[] {
    let result = new Array<TabsProps>();

    topicOverviewInfo.map(item => {
        result.push(
            {
                key: item.topicInfo.tid,
                label: (
                    <>
                        {item.topicInfo.topicName}
                        <Badge count={item.totalCount} overflowCount={9999} offset={[10, -20]}/>
                    </>
                ),
                children: <ArticleSummaryTabPane orderType={ArticleSummaryOrderType.GLOBAL}
                                                 articleSummaryInfo={articleSummaryInfo}
                                                 onPageChange={(currentTopicId: string, currentCategoryId: string, page: number, pageSize: number) => {
                                                     HomePageService.fetchArticleSummaryByTid(currentTopicId, page, pageSize, (data) => {
                                                         updateArticleSummaryInfo(data?.main);
                                                     });
                                                 }}/>,
            } as TabsProps
        );
    });

    // 确保有文章挂载的 Topic 是第一个 tab 页，而不是将 “句子收藏” 等初始化为 tab 首页
    if (result.length == 0) {
        return result;
    }

    result.push(
        {
            key: "句子收藏",
            label: (
                <>
                    句子收藏
                    <Badge count={quoteInfo.totalCount} overflowCount={9999} offset={[10, -20]}/>
                </>
            ),
            children: <QuoteTabPane quoteInfo={quoteInfo} onPageChange={(page: number, pageSize: number) => {
                HomePageService.fetchQuote(page, pageSize, (data) => {
                    updateQuoteInfo(data?.main);
                });
            }}/>,
        } as TabsProps
    );

    result.push(
        {
            key: "搜索结果",
            label: (
                <>
                    搜索结果
                    <Badge
                        count={indexSearchType == IndexSearchType.ARTICLE ? articleSummarySearchInfo.totalCount : quoteSearchInfo.totalCount}
                        overflowCount={9999}
                        offset={[10, -20]}/>
                </>
            ),
            children: <SearchResultTabPane searchType={indexSearchType}
                                           articleSummaryInfo={articleSummarySearchInfo}
                                           orderType={articleSummarySearchOrderType}
                                           quoteInfo={quoteSearchInfo}/>,
        } as TabsProps
    );

    result.push(
        {
            key: "公告",
            label: (
                <>
                    公告
                    <Badge count={announcementInfos.length} overflowCount={9999}
                           offset={[10, -20]}/>
                </>
            ),
            children: <AnnouncementTabPane announcementDtoList={announcementInfos}/>,
        } as TabsProps
    );
    return result;
}

export default IndexMainView;