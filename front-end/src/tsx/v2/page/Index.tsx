import React, {createContext, useMemo, useState} from 'react';
import {Layout} from "antd";
import IndexLeft from "../component/index/IndexLeft";
import IndexRight from "../component/index/IndexRight";

import "../../../style/index.less"
import {TopicOverviewInfo} from "../../rest/ApiClient";

export interface IndexState {
    // 菜单是否折叠收起
    menuFolded: boolean;
    updateMenuFolded: Function;
    // 文章类别目录是否折叠收起
    categoryFolded: boolean;
    updateCategoryFolded: Function;
    topicOverviewInfos: TopicOverviewInfo[];
    updateTopicOverviewInfos: Function;
}


function Index({topicOverviewInfo}: any) {
    const [menuFolded, updateMenuFolded] = useState(true);
    const [categoryFolded, updateCategoryFolded] = useState(false);
    const [topicOverviewInfos, updateTopicOverviewInfos] = useState([]);

    const state = useMemo(() => ({
        menuFolded: menuFolded,
        updateMenuFolded: updateMenuFolded,
        categoryFolded: categoryFolded,
        updateCategoryFolded: updateCategoryFolded,
        topicOverviewInfos: topicOverviewInfo,
        updateTopicOverviewInfos: updateTopicOverviewInfos,
    }), [menuFolded, categoryFolded, topicOverviewInfos]);

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