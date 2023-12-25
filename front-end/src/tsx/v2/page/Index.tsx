import React, {createContext, useMemo, useState} from 'react';
import {Layout} from "antd";
import IndexLeft from "../component/index/IndexLeft";
import IndexRight from "../component/index/IndexRight";

import "../../../style/index.less"
import {TopicOverviewInfo, UserDto} from "../../rest/ApiClient";

export interface IndexState {
    // 菜单是否折叠收起
    menuFolded: boolean;
    updateMenuFolded: Function;
    // 文章类别目录是否折叠收起
    categoryFolded: boolean;
    updateCategoryFolded: Function;
    // 已登陆的用户信息
    currentUser?: UserDto | null,
    updateCurrentUser: Function;
    // 当前选中查看的文章板块 tid
    currentTopicId?: string | null;
    updateCurrentTopicId: Function;
    topicOverviewInfos: TopicOverviewInfo[];
    updateTopicOverviewInfos: Function;
}


function Index({topicOverviewInfo}: any) {
    const [menuFolded, updateMenuFolded] = useState(true);
    const [categoryFolded, updateCategoryFolded] = useState(false);
    const [currentUser, updateCurrentUser] = useState(null);
    // 请求远端成功必然文章目录元素大于 0
    const [currentTopicId, updateCurrentTopicId] = useState(topicOverviewInfo[0].topicInfo.tid);
    const [topicOverviewInfos, updateTopicOverviewInfos] = useState([]);

    const state = useMemo(() => ({
        menuFolded: menuFolded,
        updateMenuFolded: updateMenuFolded,
        categoryFolded: categoryFolded,
        updateCategoryFolded: updateCategoryFolded,
        currentUser: currentUser,
        updateCurrentUser: updateCurrentUser,
        currentTopicId: currentTopicId,
        updateCurrentTopicId: updateCurrentTopicId,
        topicOverviewInfos: topicOverviewInfo,
        updateTopicOverviewInfos: updateTopicOverviewInfos,
    }), [menuFolded, categoryFolded, currentUser, currentTopicId, topicOverviewInfos]);

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