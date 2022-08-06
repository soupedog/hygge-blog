import * as React from "react"
import {LogHelper, UrlHelper} from '../utils/UtilContainer';
import {ArticleDto, ArticleService, UserService} from "../rest/ApiClient";
import {EditArticleContainerContext} from "./context/HyggeContext";
import {message} from "antd";

// 描述该组件 props 数据类型
export interface EditArticleContainerProps {
}

// 描述该组件 states 数据类型
export interface EditArticleContainerState {
    currentArticle?: ArticleDto
}

export class EditArticleContainer extends React.Component<EditArticleContainerProps, EditArticleContainerState> {
    constructor(props: EditArticleContainerProps) {
        super(props);
        this.state = {
            currentArticle: undefined
        };

        let currentUser = UserService.getCurrentUser();
        if (currentUser == null) {
            message.warn("未登录用户无权访问，2 秒内自动为您跳转至主页")
            UrlHelper.openNewPage({inNewTab: false, delayTime: 2000})
        }

        LogHelper.info({className: "EditArticleContainer", msg: "初始化成功"});
    }

    render() {
        return (
            <EditArticleContainerContext.Provider value={this.state}>
                <h1>文章编辑页-{this.state.currentArticle?.title}</h1>
            </EditArticleContainerContext.Provider>
        );
    }

    componentDidMount() {
        let aid: string | null = UrlHelper.getQueryString("aid");

        ArticleService.findArticleByAid(aid, (data) => {
            if (data != null) {
                this.updateRootStatus({
                    currentArticle: data.main
                })
            }
        });
    }

    updateRootStatus(deltaInfo: EditArticleContainerState) {
        this.setState(deltaInfo);
    }
}