import * as React from "react"
import {LogHelper, PropertiesHelper, UrlHelper} from '../utils/UtilContainer';
import {ArticleDto, ArticleService, UserDto, UserService} from "../rest/ApiClient";
import {Browser} from "./component/browser/Browser";
import {ReactRouter, withRouter} from "../utils/ReactRouterHelper";
import "./../../css/default.css"
import 'APlayer/dist/APlayer.min.css';
import "./../../css/browser.less"
import {message} from "antd";

// 描述该组件 props 数据类型
export interface ArticleBrowserContainerProps {
    router: ReactRouter;
}

// 描述该组件 states 数据类型
export interface ArticleBrowserContainerStatus {
    currentArticle: ArticleDto,
    currentUser?: UserDto | null,
}

class ArticleBrowserContainer extends React.Component<ArticleBrowserContainerProps, ArticleBrowserContainerStatus> {
    constructor(props: ArticleBrowserContainerProps) {
        super(props);
        this.state = {
            currentUser: UserService.getCurrentUser(),
            currentArticle: {} as ArticleDto
        };
        LogHelper.info({className: "ArticleBrowserContainer", msg: "初始化成功"});
    }

    render() {
        if (PropertiesHelper.isStringNotEmpty(this.state.currentArticle.content)) {
            return (
                <Browser isMaintainer={this.state.currentUser != null} currentArticle={this.state.currentArticle}/>
            );
        } else {
            return null;
        }
    }

    componentDidMount() {
        let _react = this;
        ArticleService.findArticleByAid(_react.props.router.params.aid, (data) => {
            if (data?.main == null) {
                message.info("目标文章不存在，2 秒内自动跳转回主页，请稍后", 2000);
                UrlHelper.openNewPage({inNewTab: false, delayTime: 2000});
            } else {
                _react.setState({
                    currentArticle: data?.main!
                });
            }
        })
    }
}

export default withRouter(ArticleBrowserContainer)