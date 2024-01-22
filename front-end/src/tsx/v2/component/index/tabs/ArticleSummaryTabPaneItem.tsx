import React from 'react';
import {Badge, List, Space} from "antd";
import clsx from "clsx";
import {DashboardTwoTone, EditTwoTone, EyeOutlined, EyeTwoTone, FormOutlined} from "@ant-design/icons";
import {ArticleSummaryInfo} from "../../../../rest/ApiClient";
import {ArticleSummaryOrderType} from "../../properties/GlobalEnum";
import {PropertiesHelper, TimeHelper, UrlHelper} from "../../../util/UtilContainer";

function ArticleSummaryTabPaneItem({isAuthor, secretKey, orderType, articleSummary}: {
    isAuthor: Boolean,
    secretKey: string | null,
    orderType: ArticleSummaryOrderType,
    articleSummary: ArticleSummaryInfo
}) {
    switch (orderType) {
        case ArticleSummaryOrderType.CATEGORY:
            if (articleSummary.orderCategory > 0) {
                return (
                    <Badge.Ribbon text="顶置" color="red">
                        {renderCore(isAuthor, secretKey, articleSummary)}
                    </Badge.Ribbon>
                );
            }
            break;
        case ArticleSummaryOrderType.GLOBAL:
            if (articleSummary.orderGlobal > 0) {
                return (
                    <Badge.Ribbon text="顶置" color="red">
                        {renderCore(isAuthor, secretKey, articleSummary)}
                    </Badge.Ribbon>
                );
            }
            break;
    }
    if (articleSummary.articleState == "PRIVATE") {
        return (
            <Badge.Ribbon text="个人" color="blue">
                {renderCore(isAuthor, secretKey, articleSummary)}
            </Badge.Ribbon>
        );
    } else {
        return renderCore(isAuthor, secretKey, articleSummary);
    }
}

function createFooterItemList({isAuthor, aid, wordCount, pageViews, selfPageViews, createTs}: {
    isAuthor: Boolean,
    aid: string,
    wordCount: number,
    pageViews: number,
    selfPageViews: number,
    createTs: number
}) {
    if (isAuthor) {
        return (
            [
                <IconText icon={EditTwoTone} text={"字数 " + wordCount}
                          key={"word_count_" + aid}/>,
                <IconText icon={DashboardTwoTone}
                          text={"创建于 " + TimeHelper.formatTimeStampToString(createTs)}
                          key={"create_ts_" + aid}/>,
                <IconText icon={EyeTwoTone} text={"浏览量 " + pageViews}
                          key={"page_view_" + aid}/>,
                <IconText icon={EyeOutlined} text={"自浏览 " + selfPageViews}
                          key={"self_view_" + aid}/>
            ]
        );
    } else {
        return (
            [
                <IconText icon={EditTwoTone} text={"字数 " + wordCount}
                          key={"word_count_" + aid}/>,
                <IconText icon={DashboardTwoTone}
                          text={TimeHelper.formatTimeStampToString(createTs)}
                          key={"create_ts_" + aid}/>,
                <IconText icon={EyeTwoTone} text={"浏览量 " + pageViews}
                          key={"page_view_" + aid}/>,
            ]
        );
    }
}

function getCategoryInfo(articleSummary: ArticleSummaryInfo): string {
    let result = articleSummary.categoryTreeInfo.topicInfo.topicName;

    articleSummary.categoryTreeInfo.categoryList.forEach((item) => {
        result = result + " / " + item.categoryName
    });
    return result;
}

function renderCore(isAuthor: Boolean, secretKey: string | null, articleSummary: ArticleSummaryInfo) {
    let isDraft = articleSummary.articleState == "DRAFT";
    return (
        <List.Item
            key={articleSummary.title}
            actions={createFooterItemList({
                isAuthor: isAuthor,
                aid: articleSummary.aid,
                wordCount: articleSummary.wordCount,
                pageViews: articleSummary.pageViews,
                selfPageViews: articleSummary.selfPageViews,
                createTs: articleSummary.createTs
            })}
            extra={
                <img
                    width={272}
                    alt="logo"
                    src={articleSummary.imageSrc}
                />
            }
        >
            <List.Item.Meta
                title={
                    <>
                        <a className={
                            clsx({
                                "draftHighlight": isDraft
                            })
                        }
                           style={{fontSize: "32px", fontWeight: 900, lineHeight: "40px"}}
                           href={
                               PropertiesHelper.isStringNotEmpty(secretKey) ? UrlHelper.getBaseUrl() + "browser/" + articleSummary.aid + "?secretKey=" + secretKey
                                   : UrlHelper.getBaseUrl() + "browser/" + articleSummary.aid
                           }
                           target="_blank">{articleSummary.title}{isDraft ? "【草稿】" : null}</a>
                        {
                            isAuthor ? <EditIcon icon={FormOutlined} text={"编辑"}
                                                 aid={articleSummary.aid}
                                                 key={"edit_" + articleSummary.aid}></EditIcon> : null
                        }
                    </>
                }
                description={getCategoryInfo(articleSummary)}
            />
            <div style={{textIndent: "2em", fontSize: "14px", lineHeight: "24px"}}>
                {articleSummary.summary}
            </div>
        </List.Item>
    );
}

const EditIcon = ({icon, text, aid}: { icon: React.FC; text: string, aid: string }) => (
    <Space className={"pointer"}
           onClick={() => {
               UrlHelper.openNewPage({inNewTab: false, path: "editor/article/" + aid})
           }}
           style={{
               float: "right",
               marginRight: "20px",
               fontSize: "14px"
           }}>
        {React.createElement(icon)}
        {text}
    </Space>
);

const IconText = ({icon, text}: { icon: React.FC; text: string }) => (
    <Space>
        {React.createElement(icon)}
        {text}
    </Space>
);



export default ArticleSummaryTabPaneItem;