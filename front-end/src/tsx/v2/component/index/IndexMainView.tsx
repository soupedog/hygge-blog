import React from 'react';
import {Badge, Tabs, TabsProps} from "antd";
import {IndexContext} from '../../page/Index';
import {AnnouncementDto, ArticleSummaryResponse, TopicOverviewInfo} from "../../../rest/ApiClient";
import AnnouncementTabPane from "./AnnouncementTabPane";
import ArticleSummaryContainer from "./ArticleSummaryContainer";
import {ArticleSummaryOrderType} from "./ArticleSummaryItem";

function createTabs({topicOverviewInfo, articleSummaryInfo, announcementInfos}: {
    topicOverviewInfo: TopicOverviewInfo[],
    articleSummaryInfo: ArticleSummaryResponse,
    announcementInfos: AnnouncementDto[]
}): TabsProps[] {
    let result = new Array<TabsProps>();

    topicOverviewInfo.map(item => {
        result.push(
            {
                key: item.topicInfo.topicName,
                label: (
                    <>
                        {item.topicInfo.topicName}
                        <Badge count={item.totalCount} overflowCount={9999} offset={[10, -20]}/>
                    </>
                ),
                children: <ArticleSummaryContainer orderType={ArticleSummaryOrderType.GLOBAL}
                                                   articleSummaryInfo={articleSummaryInfo}/>,
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
                    <Badge count={0} overflowCount={9999} offset={[10, -20]}/>
                </>
            ),
            children: <div style={{height: "500px", backgroundColor: "red"}}></div>,
        } as TabsProps
    );


    result.push(
        {
            key: "搜索结果",
            label: (
                <>
                    搜索结果
                    <Badge count={0} overflowCount={9999} offset={[10, -20]}/>
                </>
            ),
            children: <div style={{height: "500px", backgroundColor: "red"}}></div>,
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


function IndexMainView() {
    return (
        <IndexContext.Consumer>
            {({
                  updateCategoryFolded,
                  topicOverviewInfos,
                  articleSummaryInfo,
                  updateArticleSummaryInfo,
                  announcementInfos
              }) => (
                <Tabs type="card" size={"large"}
                    // @ts-ignore
                      items={createTabs({
                          topicOverviewInfo: topicOverviewInfos,
                          articleSummaryInfo: articleSummaryInfo,
                          announcementInfos: announcementInfos
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
                              // state.updateRootStatus!({
                              //     categoryFolded: false,
                              //     currentTid: key.substring(8)
                              // });
                          }
                      }}>
                </Tabs>
            )}
        </IndexContext.Consumer>
    );
}

export default IndexMainView;