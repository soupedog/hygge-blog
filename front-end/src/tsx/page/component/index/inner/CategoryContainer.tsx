import * as React from "react"
import {LogHelper} from '../../../../utils/UtilContainer';
import {Badge, Card, Collapse} from "antd";
import {IndexContainerState} from "../../../IndexContainer";
import {IndexContainerContext} from "../../../context/HyggeContext";
import {TopicOverviewInfo} from "../../../../rest/ApiClient";

const {Panel} = Collapse;

// 描述该组件 props 数据类型
export interface CategoryContainerProps {
}

// 描述该组件 states 数据类型
export interface CategoryContainerStatus {
}

const gridStyle: React.CSSProperties = {
    width: '20%',
    padding: "10px",
    textAlign: 'center',
};

export class CategoryContainer extends React.Component<CategoryContainerProps, CategoryContainerStatus> {
    constructor(props: CategoryContainerProps) {
        super(props);
        this.state = {};
        LogHelper.info({className: "CategoryContainer", msg: "初始化成功"});
    }

    render() {
        return (
            <IndexContainerContext.Consumer>
                {(state: IndexContainerState) => (
                    <Collapse activeKey={[state.categoryFolded ? "folded" : "default"]}>
                        <Panel header="文章类别目录" key={"default"}>
                            <Card size={"small"}>
                                {
                                    this.renderCategoryItem(state)
                                }
                            </Card>
                        </Panel>
                    </Collapse>
                )}
            </IndexContainerContext.Consumer>
        );
    }

    renderCategoryItem(state: IndexContainerState) {
        let currentTid: string | undefined = state.currentTid;
        let topicOverviewInfos: TopicOverviewInfo[] | undefined = state.topicOverviewInfoList;

        if (topicOverviewInfos == null) {
            return null;
        }

        let newOne = topicOverviewInfos.filter(item => {
            return item.topicInfo.tid == currentTid
        });

        if (newOne != null && newOne.length > 0) {
            return (newOne[0].categoryListInfo.map(item => {
                if (item.articleCount! <= 0) {
                    return null;
                }
                return (
                    <Card.Grid className={"pointer"} style={gridStyle} onClick={() => {
                        state.fetchSearchViewInfo!(1, 5, state, item.cid, null);
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
