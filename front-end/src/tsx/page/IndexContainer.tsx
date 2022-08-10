import * as React from "react"
import {LogHelper, PropertiesHelper} from '../utils/UtilContainer';
import zhCN from "antd/lib/locale/zh_CN";
import {ConfigProvider, Layout} from "antd";
import {IndexContainerContext} from "./context/HyggeContext";

import 'antd/dist/antd.min.css';
import '../../css/default.css';
import '../../css/index.less';
import {
    AnnouncementDto,
    HomePageService,
    QuoteResponse,
    TopicOverviewInfo,
    UserDto,
    UserService
} from "../rest/ApiClient";
import {IndexLeft} from "./component/index/IndexLeft";
import {IndexRight} from "./component/index/IndexRight";

// 描述该组件 props 数据类型
export interface IndexContainerProps {
}

// 描述该组件 states 数据类型
export interface IndexContainerState {
    currentUser?: UserDto | null,
    // 标记当前有多少网络请求
    netWorkArrayCounter?: boolean[],
    currentTid?: string,
    topicOverviewInfoList?: TopicOverviewInfo[],
    quoteResponse?: QuoteResponse;
    currentCid?: string,
    searchType?: SearchType,
    searchKey?: string,
    searchResultInfoList?: {
        viewInfoList: any[],
        totalCount: number
    },
    announcementDtoList?: AnnouncementDto[],
    // 菜单是否折叠收起
    menuFolded?: boolean,
    // 文章类别是否折叠收起
    categoryFolded?: boolean,

    fetchSearchViewInfo?: Function,
    updateRootStatus?: Function,
}

export enum SearchType {
    ARTICLE = "ARTICLE",
    QUOTE = "QUOTE"
}

export class IndexContainer extends React.Component<IndexContainerProps, IndexContainerState> {
    constructor(props: IndexContainerProps) {
        super(props);
        this.state = {
            currentUser: UserService.getCurrentUser(),
            netWorkArrayCounter: [],
            currentTid: "",
            topicOverviewInfoList: [{
                topicInfo: {
                    tid: "",
                    topicName: "编程",
                    orderVal: 0
                },
                categoryListInfo: [],
                totalCount: 0
            }],
            quoteResponse: {
                quoteList: [],
                totalCount: 0
            },
            currentCid: "",
            searchType: SearchType.ARTICLE,
            searchKey: "",
            searchResultInfoList: {
                viewInfoList: [],
                totalCount: 0
            },
            announcementDtoList: [],
            menuFolded: true,
            categoryFolded: false,
            fetchSearchViewInfo: this.fetchSearchViewInfo.bind(this),
            updateRootStatus: this.updateRootStatus.bind(this)
        };
        LogHelper.info({className: "IndexContainer", msg: "初始化成功"});
    }

    render() {
        return (
            <ConfigProvider locale={zhCN}>
                <IndexContainerContext.Provider value={this.state}>
                    <Layout>
                        <IndexLeft/>
                        <IndexRight/>
                    </Layout>
                </IndexContainerContext.Provider>
            </ConfigProvider>
        );
    }

    componentDidMount() {
        let _react = this;
        HomePageService.fetch((data) => {
            if (data?.main?.topicOverviewInfoList != null) {
                _react.updateRootStatus({
                    currentTid: data.main.topicOverviewInfoList[0].topicInfo.tid,
                    topicOverviewInfoList: data?.main?.topicOverviewInfoList
                });
            }

            HomePageService.fetchAnnouncement((data) => {
                _react.updateRootStatus({
                    announcementDtoList: data?.main
                });
            });

            HomePageService.fetchQuote(1, 1, (data) => {
                _react.updateRootStatus({
                    quoteResponse: data?.main,
                });
            });
        });
    }

    fetchSearchViewInfo(currentPage: number, pageSize: number, state: IndexContainerState,
                        currentCid?: string,
                        successHook?: (input?: any) => void) {

        if (PropertiesHelper.isStringNotEmpty(currentCid)) {
            HomePageService.fetchArticleSummaryByCid(currentCid!, currentPage, pageSize, (data) => {
                let searchViewList = {
                    viewInfoList: data?.main!.articleSummaryList!,
                    totalCount: data?.main?.totalCount!
                };

                state.updateRootStatus!({
                    currentCid: currentCid,
                    searchResultInfoList: searchViewList
                });
                if (successHook != null) {
                    successHook(searchViewList)
                }
            });
        }
    }

    updateRootStatus(deltaInfo: IndexContainerState) {
        this.setState(deltaInfo);
    }
}