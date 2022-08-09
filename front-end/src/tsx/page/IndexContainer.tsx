import * as React from "react"
import {LogHelper} from '../utils/UtilContainer';
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
    topicOverviewInfoList?: TopicOverviewInfo[],
    currentTid?: string,
    announcementDtoList?: AnnouncementDto[],
    quoteResponse?: QuoteResponse;
    // 菜单是否折叠收起
    menuFolded?: boolean,
    // 文章类别是否折叠收起
    categoryFolded?: boolean,
    searchType?: SearchType,
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
            topicOverviewInfoList: [{
                topicInfo: {
                    tid: "",
                    topicName: "编程",
                    orderVal: 0
                },
                categoryListInfo: [],
                totalCount: 0
            }],
            currentTid: "",
            announcementDtoList: [],
            quoteResponse: {
                quoteList: [],
                totalCount: 0
            },
            menuFolded: true,
            categoryFolded: false,
            searchType: SearchType.ARTICLE,
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

    updateRootStatus(deltaInfo: IndexContainerState) {
        this.setState(deltaInfo);
    }
}