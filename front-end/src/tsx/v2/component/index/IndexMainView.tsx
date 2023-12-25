import React from 'react';
import {Badge, Tabs, TabsProps} from "antd";
import {IndexContext} from '../../page/Index';
import {AnnouncementDto, TopicOverviewInfo} from "../../../rest/ApiClient";
import AnnouncementTabPane from "./AnnouncementTabPane";

function createTabs(topicOverviewInfo: TopicOverviewInfo[], announcementInfos: AnnouncementDto[]): TabsProps[] {
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
                children: <div style={{height: "500px", backgroundColor: "red"}}></div>,
            } as TabsProps
        );
    });


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
            key: "搜索",
            label: (
                <>
                    搜索
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
                    <Badge count={announcementInfos.length} overflowCount={9999} offset={[10, -20]}/>
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
            {({topicOverviewInfos, articleSummaryInfo, updateArticleSummaryInfo, announcementInfos}) => (
                <Tabs type="card" size={"large"}
                    // @ts-ignore
                      items={createTabs(topicOverviewInfos, announcementInfos)}
                      onChange={(key) => {
                          switch (key) {
                              case "句子收藏":
                              case "公告":
                                  // state.updateRootStatus!({categoryFolded: true});
                                  break;
                              case "搜索结果":
                                  break;
                              default :
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