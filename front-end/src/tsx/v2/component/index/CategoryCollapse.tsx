import React from 'react';
import {Badge, Card, Collapse} from "antd";
import {IndexContext} from '../../page/Index';
import {TopicOverviewInfo} from "../../../rest/ApiClient";

function CategoryCollapse() {
    return (
        <IndexContext.Consumer>
            {({
                  categoryFolded, updateCategoryFolded,
                  currentTopicId, updateCurrentTopicId,
                  topicOverviewInfos, updateTopicOverviewInfos
              }) => (
                // folded 是虚空组件，相当于收起所有面板
                // 为 true 时，激活 key 为 default 的面板
                <Collapse activeKey={[categoryFolded ? "folded" : "default"]}
                    // unknown 是一个不存在的文章板块
                          items={renderCategoryPanel(topicOverviewInfos, currentTopicId == null ? "unknown" : currentTopicId)}>
                </Collapse>
            )}
        </IndexContext.Consumer>
    );

    function renderCategoryPanel(infos: TopicOverviewInfo[], selectedTid: string) {
        // 只有单个子节点
        return [{
            // 默认展开 default 面板
            key: 'default',
            label: '文章类别目录',
            children: renderCategoryCard(infos, selectedTid),
        }];
    }

    function renderCategoryCard(infos: TopicOverviewInfo[], selectedTid: string) {
        return (
            <Card size={"small"}>
                {
                    renderCategoryCardGrid(infos, selectedTid)
                }
            </Card>
        );
    }

    function renderCategoryCardGrid(infos: TopicOverviewInfo[], selectedTid: string) {
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
                        // state.fetchCategoryArticleSearchViewInfo!(1, 5, state, item.cid, null);
                        document.getElementById("searchTap")?.click();
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