import React from 'react';
import {Badge, Card, Collapse} from "antd";
import {IndexContext} from '../../page/Index';
import {HomePageService, TopicOverviewInfo} from "../../rest/ApiClient";
import {ArticleSummaryOrderType, IndexSearchType} from "../properties/GlobalEnum";

function CategoryCollapse() {
    return (
        <IndexContext.Consumer>
            {({
                  categoryFolded, updateCategoryFolded,
                  currentTopicId, updateCurrentTopicId,
                  updateCurrentCategoryId,
                  topicOverviewInfos,
                  updateIndexSearchType,
                  updateArticleSummarySearchInfo,
                  updateArticleSummarySearchOrderType
              }) => (
                // folded 是虚空组件，相当于收起所有面板
                // 为 true 时，激活 key 为 default 的面板
                <Collapse activeKey={[categoryFolded ? "folded" : "default"]}
                    // unknown 是一个不存在的文章板块
                          items={renderCategoryPanel(topicOverviewInfos, currentTopicId == null ? "unknown" : currentTopicId, updateIndexSearchType, updateArticleSummarySearchOrderType, updateCurrentCategoryId, updateArticleSummarySearchInfo)}>
                </Collapse>
            )}
        </IndexContext.Consumer>
    );

    function renderCategoryPanel(infos: TopicOverviewInfo[], selectedTid: string, updateIndexSearchType: Function, updateArticleSummarySearchOrderType: Function, updateCurrentCategoryId: Function, updateArticleSummarySearchInfo: Function) {
        // 只有单个子节点
        return [{
            // 默认展开 default 面板
            key: 'default',
            label: '文章类别目录',
            children: renderCategoryCard(infos, selectedTid, updateIndexSearchType, updateArticleSummarySearchOrderType, updateCurrentCategoryId, updateArticleSummarySearchInfo),
        }];
    }

    function renderCategoryCard(infos: TopicOverviewInfo[], selectedTid: string, updateIndexSearchType: Function, updateArticleSummarySearchOrderType: Function, updateCurrentCategoryId: Function, updateArticleSummarySearchInfo: Function) {
        return (
            <Card size={"small"}>
                {
                    renderCategoryCardGrid(infos, selectedTid, updateIndexSearchType, updateArticleSummarySearchOrderType, updateCurrentCategoryId, updateArticleSummarySearchInfo)
                }
            </Card>
        );
    }

    function renderCategoryCardGrid(infos: TopicOverviewInfo[], selectedTid: string, updateIndexSearchType: Function, updateArticleSummarySearchOrderType: Function, updateCurrentCategoryId: Function, updateArticleSummarySearchInfo: Function) {
        let topicOverviewInfos: TopicOverviewInfo[] | undefined = infos;

        if (topicOverviewInfos == null || topicOverviewInfos.length < 1) {
            return null;
        }

        let newOne = topicOverviewInfos.filter(item => {
            return item.topicInfo.tid == selectedTid
        });

        if (newOne != null && newOne.length > 0) {
            return (newOne[0].categoryListInfo.map(item => {
                if (item.articleCount! <= 0) {
                    return null;
                }
                return (
                    <Card.Grid className={"pointer"} style={gridStyle} onClick={() => {
                        updateIndexSearchType(IndexSearchType.ARTICLE);
                        updateArticleSummarySearchOrderType(ArticleSummaryOrderType.CATEGORY);
                        updateCurrentCategoryId(item.cid);

                        HomePageService.fetchArticleSummaryByCid(item.cid, 1, 5, (data) => {
                            updateArticleSummarySearchInfo(data?.main);
                            // 切换到搜索展示页
                            document.getElementById("rc-tabs-0-tab-搜索结果")?.click();
                        });
                    }} key={"card_" + item.categoryName}>
                        <Badge.Ribbon style={{top: "-10px"}} text={item.articleCount == null ? "" : item.articleCount}
                                      color="red">
                            <div style={{padding: "0 15px 0 15px"}}>
                                {item.categoryName}
                            </div>
                        </Badge.Ribbon>
                    </Card.Grid>
                );
            }))
        } else {
            return null;
        }
    }
}

const gridStyle: React.CSSProperties = {
    width: '20%',
    padding: "10px",
    textAlign: 'center',
};

export default CategoryCollapse;