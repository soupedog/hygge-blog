import * as React from "react"
import {LogHelper, TimeHelper, UrlHelper} from '../../../../utils/UtilContainer';
import {Badge, List, Space} from 'antd';
import {ArticleSummaryInfo} from "../../../../rest/ApiClient";
import {DashboardTwoTone, EditTwoTone, EyeOutlined, EyeTwoTone, FormOutlined} from "@ant-design/icons";

// 描述该组件 props 数据类型
export interface ArticleViewItemProps {
    currentArticle: ArticleSummaryInfo,
    isMaintainer: boolean,
    topicQuery: boolean
}

// 描述该组件 states 数据类型
export interface ArticleViewItemStatus {
}

export class ArticleOverviewViewItem extends React.Component<ArticleViewItemProps, ArticleViewItemStatus> {
    constructor(props: ArticleViewItemProps) {
        super(props);
        this.state = {};
        LogHelper.info({className: "ArticleOverviewViewItem", msg: "初始化成功"});
    }

    render() {
        let _react = this;

        if (_react.props.topicQuery) {
            if (_react.props.currentArticle.orderGlobal > 0) {
                return (
                    <Badge.Ribbon text="顶置" color="red">
                        {_react.renderCore()}
                    </Badge.Ribbon>
                );
            } else {
                return _react.renderCore();
            }
        } else {
            if (_react.props.currentArticle.orderCategory > 0) {
                return (
                    <Badge.Ribbon text="顶置" color="red">
                        {_react.renderCore()}
                    </Badge.Ribbon>
                );
            } else {
                return _react.renderCore();
            }
        }
    }

    renderCore() {
        let _react = this;
        return (
            <List.Item
                key={this.props.currentArticle.title}
                actions={this.createFooterItemList()}
                extra={
                    <img
                        width={272}
                        alt="logo"
                        src={this.props.currentArticle.imageSrc}
                    />
                }
            >
                <List.Item.Meta
                    title={
                    <>
                        <a style={{fontSize: "32px", fontWeight: 900, lineHeight: "40px"}}
                           href={UrlHelper.getBaseUrl() + "#/browser/" + this.props.currentArticle.aid}
                           target="_blank">{this.props.currentArticle.title}</a>
                        {
                            this.props.isMaintainer ? <EditIcon icon={FormOutlined} text={"编辑"}
                                                                aid={this.props.currentArticle.aid}
                                                                key={"edit_" + this.props.currentArticle.aid}></EditIcon> : null
                        }
                    </>
                }
                    description={_react.getCategoryInfo(this.props.currentArticle)}
                />
                <div style={{textIndent: "2em", fontSize: "14px", lineHeight: "24px"}}>
                    {this.props.currentArticle.summary}
                </div>
            </List.Item>
        );
    }

    getCategoryInfo(articleSummary: ArticleSummaryInfo): string {
        let result = articleSummary.categoryTreeInfo.topicInfo.topicName;

        articleSummary.categoryTreeInfo.categoryList.forEach((item) => {
            result = result + " / " + item.categoryName
        });
        return result;
    }

    createFooterItemList() {
        if (this.props.isMaintainer) {
            return (
                [
                    <IconText icon={EditTwoTone} text={"字数 " + this.props.currentArticle.wordCount}
                              key={"word_count_" + this.props.currentArticle.aid}/>,
                    <IconText icon={DashboardTwoTone}
                              text={TimeHelper.formatTimeStampToString(this.props.currentArticle.createTs)}
                              key={"create_ts_" + this.props.currentArticle.aid}/>,
                    <IconText icon={EyeTwoTone} text={"浏览量 " + this.props.currentArticle.pageViews}
                              key={"page_view_" + this.props.currentArticle.aid}/>,
                    <IconText icon={EyeOutlined} text={"自浏览 " + this.props.currentArticle.selfPageViews}
                              key={"self_view_" + this.props.currentArticle.aid}/>
                ]
            );
        } else {
            return (
                [
                    <IconText icon={EditTwoTone} text={"字数 " + this.props.currentArticle.wordCount}
                              key={"word_count_" + this.props.currentArticle.aid}/>,
                    <IconText icon={DashboardTwoTone}
                              text={TimeHelper.formatTimeStampToString(this.props.currentArticle.createTs)}
                              key={"create_ts_" + this.props.currentArticle.aid}/>,
                    <IconText icon={EyeTwoTone} text={"浏览量 " + this.props.currentArticle.pageViews}
                              key={"page_view_" + this.props.currentArticle.aid}/>,
                ]
            );
        }
    }
}

const IconText = ({icon, text}: { icon: React.FC; text: string }) => (
    <Space>
        {React.createElement(icon)}
        {text}
    </Space>
);

const EditIcon = ({icon, text, aid}: { icon: React.FC; text: string, aid: string }) => (
    <Space className={"pointer"} onClick={() => {
        UrlHelper.openNewPage({inNewTab: false, path: "#/editor/article/" + aid})
    }} style={{
        float: "right",
        marginRight: "20px",
        fontSize: "14px"
    }}>
        {React.createElement(icon)}
        {text}
    </Space>
);