import * as React from "react"
import {LogHelper, PropertiesHelper} from '../utils/UtilContainer';
import {ArticleDto, ArticleService} from "../rest/ApiClient";
import {Browser} from "./component/browser/Browser";
import {ReactRouter, withRouter} from "../utils/ReactRouterHelper";

// 描述该组件 props 数据类型
export interface ArticleBrowserContainerProps {
    router: ReactRouter;
}

// 描述该组件 states 数据类型
export interface ArticleBrowserContainerStatus {
    currentArticle: ArticleDto
}

class ArticleBrowserContainer extends React.Component<ArticleBrowserContainerProps, ArticleBrowserContainerStatus> {
    constructor(props: ArticleBrowserContainerProps) {
        super(props);

        this.state = {
            currentArticle: {} as ArticleDto
        };
        LogHelper.info({className: "ArticleBrowserContainer", msg: "初始化成功"});
    }

    render() {
        if (PropertiesHelper.isStringNotEmpty(this.state.currentArticle.content)) {
            return (
                <Browser currentArticle={this.state.currentArticle}/>
            );
        } else {
            return null;
        }
    }

    componentDidMount() {
        let _react = this;
        ArticleService.findArticleByAid(_react.props.router.params.aid, (data) => {
            _react.setState({
                currentArticle: data?.main!
            });
        })
    }
}

export default withRouter(ArticleBrowserContainer)