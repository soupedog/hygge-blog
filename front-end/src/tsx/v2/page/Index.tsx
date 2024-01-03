import React, {createContext, useEffect, useMemo, useState} from 'react';
import {ConfigProvider, Layout} from "antd";
import IndexLeft from "../component/index/IndexLeft";
import IndexRight from "../component/index/IndexRight";

import "../../../style/index.less"
import {
    AnnouncementDto,
    ArticleSummaryResponse,
    HomePageService,
    QuoteResponse,
    TopicOverviewInfo
} from "../../rest/ApiClient";
import zhCN from "antd/lib/locale/zh_CN";

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
    articleSummaryInfo: ArticleSummaryResponse;
    updateArticleSummaryInfo: Function;
    quoteInfo: QuoteResponse;
    updateQuoteInfo: Function;
    announcementInfos: AnnouncementDto[];
    updateAnnouncementInfos: Function;
}


function Index() {
    const [menuFolded, updateMenuFolded] = useState(true);
    const [categoryFolded, updateCategoryFolded] = useState(false);
    // 请求远端成功必然文章目录元素大于 0
    const [currentTopicId, updateCurrentTopicId] = useState("");
    const [topicOverviewInfos, updateTopicOverviewInfos] = useState([]);

    const [articleSummaryInfo, updateArticleSummaryInfo] = useState(
        {
            articleSummaryList: [],
            totalCount: 0
        } as ArticleSummaryResponse
    );
    const [quoteInfo, updateQuoteInfo] = useState(
        {
            quoteList: [],
            totalCount: 0
        } as QuoteResponse
    );
    const [announcementInfos, updateAnnouncementInfos] = useState([]);

    const state = useMemo(() => ({
        menuFolded: menuFolded,
        updateMenuFolded: updateMenuFolded,
        categoryFolded: categoryFolded,
        updateCategoryFolded: updateCategoryFolded,
        currentTopicId: currentTopicId,
        updateCurrentTopicId: updateCurrentTopicId,
        topicOverviewInfos: topicOverviewInfos,
        updateTopicOverviewInfos: updateTopicOverviewInfos,
        articleSummaryInfo: articleSummaryInfo,
        updateArticleSummaryInfo: updateArticleSummaryInfo,
        quoteInfo: quoteInfo,
        updateQuoteInfo: updateQuoteInfo,
        announcementInfos: announcementInfos,
        updateAnnouncementInfos: updateAnnouncementInfos
    }), [menuFolded, categoryFolded, currentTopicId, topicOverviewInfos, articleSummaryInfo, quoteInfo, announcementInfos]);

    useEffect(() => {
        HomePageService.fetch(data => {
            let response = data!.main!;
            let firstInitTopicOverviewInfo = response.topicOverviewInfoList;

            // @ts-ignore
            updateTopicOverviewInfos(firstInitTopicOverviewInfo);
            updateCurrentTopicId(firstInitTopicOverviewInfo[0].topicInfo.tid);
            updateArticleSummaryInfo(response.articleSummaryInfo);
            updateQuoteInfo(response.quoteInfo);
            // @ts-ignore
            updateAnnouncementInfos(response.announcementInfoList);
        });
        // 依赖静态值表示仅初始化时调用一次
    }, []);

    return (
        <ConfigProvider locale={zhCN}>
            <IndexContext.Provider value={state}>
                <Layout>
                    <IndexLeft/>
                    <IndexRight/>
                </Layout>
            </IndexContext.Provider>
        </ConfigProvider>
    );
}

export const IndexContext = createContext<IndexState>({} as IndexState);
export default Index;