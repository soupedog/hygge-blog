import React, {createContext, useEffect, useMemo, useState} from 'react';
import {Layout} from "antd";
import IndexLeft from "../component/index/IndexLeft";
import IndexRight from "../component/index/IndexRight";

import "../../../style/index.less"
import {HomePageService, TopicOverviewInfo} from "../../rest/ApiClient";

export interface IndexState {
    // 菜单是否折叠收起
    menuFolded: boolean;
    updateMenuFolded: Function;
    // 文章类别目录是否折叠收起
    categoryFolded: boolean;
    updateCategoryFolded: Function;
    // 当前选中查看的文章板块 tid
    currentTopicId?: string | null;
    updateCurrentTopicId: Function;
    topicOverviewInfos: TopicOverviewInfo[];
    updateTopicOverviewInfos: Function;
}


function Index() {
    const [menuFolded, updateMenuFolded] = useState(true);
    const [categoryFolded, updateCategoryFolded] = useState(false);
    // 请求远端成功必然文章目录元素大于 0
    const [currentTopicId, updateCurrentTopicId] = useState("");
    const [topicOverviewInfos, updateTopicOverviewInfos] = useState([]);

    const state = useMemo(() => ({
        menuFolded: menuFolded,
        updateMenuFolded: updateMenuFolded,
        categoryFolded: categoryFolded,
        updateCategoryFolded: updateCategoryFolded,
        currentTopicId: currentTopicId,
        updateCurrentTopicId: updateCurrentTopicId,
        topicOverviewInfos: topicOverviewInfos,
        updateTopicOverviewInfos: updateTopicOverviewInfos,
    }), [menuFolded, categoryFolded, currentTopicId, topicOverviewInfos]);

    useEffect(() => {
        // 成功获取到初始化数据后再开始渲染页面
        HomePageService.fetch(data => {
            let firstInitTopicOverviewInfo = data!.main!.topicOverviewInfoList;

            // @ts-ignore
            updateTopicOverviewInfos(firstInitTopicOverviewInfo);
            updateCurrentTopicId(firstInitTopicOverviewInfo[0].topicInfo.tid);
        });
        // 依赖静态值表示仅初始化时调用一次
    }, []);

    return (
        <IndexContext.Provider value={state}>
            <Layout>
                <IndexLeft/>
                <IndexRight/>
            </Layout>
        </IndexContext.Provider>
    );
}

export const IndexContext = createContext<IndexState>({} as IndexState);
export default Index;