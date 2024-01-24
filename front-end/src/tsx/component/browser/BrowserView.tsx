import React, {useEffect, useState} from 'react';
import MusicPlayer from "../player/MusicPlayer";
import {ArticleDto, UserService} from "../../rest/ApiClient";
import {Affix, Breadcrumb, Card, FloatButton, Layout, message, Space, Tree} from "antd";
import {DashboardTwoTone, DownOutlined, EditTwoTone, EyeOutlined, EyeTwoTone} from '@ant-design/icons';
import HyggeFooter from "../HyggeFooter";
import HyggeBrowserHeader from "./HyggeBrowserHeader";
import {Content} from "antd/es/layout/layout";
import Sider from "antd/es/layout/Sider";
import {TreeProps} from "antd/es/tree/Tree";
import {AntdTreeNodeInfo, MdHelper} from "../markdown/util/MdHelper";
import {class_md_preview} from "../properties/ElementNameContainer";
import remarkGfm from "remark-gfm";
import remarkMath from "remark-math";
import rehypeKatex from "rehype-katex";
import rehypeSlug from "rehype-slug";
import rehypeRaw from "rehype-raw";
import rehypeHighlight from "rehype-highlight";
import bash from 'highlight.js/lib/languages/bash';
import shell from 'highlight.js/lib/languages/shell'
import dockerfile from 'highlight.js/lib/languages/dockerfile';
import nginx from 'highlight.js/lib/languages/nginx';
import javascript from 'highlight.js/lib/languages/javascript';
import typescript from 'highlight.js/lib/languages/typescript';
import java from 'highlight.js/lib/languages/java';
import python from 'highlight.js/lib/languages/python';
import sql from 'highlight.js/lib/languages/sql';
import properties from 'highlight.js/lib/languages/properties';
import json from 'highlight.js/lib/languages/json';
import xml from 'highlight.js/lib/languages/xml';
import yaml from 'highlight.js/lib/languages/yaml';
import ReactMarkdown from "react-markdown";
import {TimeHelper} from "../../util/UtilContainer";

function BrowserView({article}: { article: ArticleDto | null }) {
    const [tocEnable, updateTocEnable] = useState(true);
    const [tocTree, updateTocTree] = useState([]);

    useEffect(() => {
        // 1 秒后刷新目录
        // 因为 markdown 插件目前好像没有回调函数
        // react 的 hook 模式也没有了 componentDidMount
        window.setTimeout(function () {
            initToc(updateTocTree, updateTocEnable, true);
        }, 1000);

        // 依赖静态值表示仅初始化时调用一次
    }, []);

    return (
        <Layout>
            <HyggeBrowserHeader/>
            {renderMainImage(article)}
            {renderMusicPlayer(article)}
            {renderArticle(article, tocEnable, updateTocEnable, tocTree, updateTocTree)}
            <HyggeFooter/>
        </Layout>
    );
}

function renderMainImage(article: ArticleDto | null) {
    if (article != null) {
        return (
            <div style={{
                width: "100%",
                height: "400px",
                backgroundSize: "cover",
                background: "url(" + article.imageSrc + ") no-repeat center"
            }}/>
        );
    }
}

function renderMusicPlayer(article: ArticleDto | null) {
    if (article != null) {
        return (
            <MusicPlayer configuration={article.configuration}/>
        );
    }
}

function renderBreadcrumbItems(article: ArticleDto | null) {
    let result = new Array();
    if (article != null) {
        // 主题名称
        result.push(
            {
                title: article.categoryTreeInfo.topicInfo.topicName,
            }
        );

        // 文章类别名称
        article.categoryTreeInfo.categoryList.forEach((articleCategoryInfo, index) => {
            result.push(
                {
                    title: articleCategoryInfo.categoryName,
                }
            );
        })
    }
    return result;
}

function initToc(updateTocTree: Function, updateTocEnable: Function, nextTocEnable: Boolean) {
    let antdTreeNodeInfos: Array<AntdTreeNodeInfo> = new Array<AntdTreeNodeInfo>();
    let map: Map<number, AntdTreeNodeInfo> = new Map<number, AntdTreeNodeInfo>();

    document.querySelectorAll("h1,h2,h3,h4,h5,h6").forEach((item, index) => {
        let antdTreeNode = {
            index: index,
            key: "toc_" + index,
            nodeName: item.tagName,
            level: null,
            title: item.textContent as string,
            value: item.id,
            parentNodeIndex: null,
            children: new Array<AntdTreeNodeInfo>
        };

        antdTreeNodeInfos.push(antdTreeNode);
        map.set(index, antdTreeNode);
    });

    let currentTOC = MdHelper.initTitleTree({
        currentTOCArray: antdTreeNodeInfos,
        allTocNodeMap: map,
        errorCallback: null
    });

    if (currentTOC.length > 0) {
        updateTocTree(currentTOC);
        updateTocEnable(nextTocEnable);
    } else {
        message.info("未找到目录结构");
    }
}

function renderArticle(article: ArticleDto | null, tocEnable: Boolean, updateTocEnable: Function, tocTree: AntdTreeNodeInfo[], updateTocTree: Function) {
    if (article != null) {
        let isAuthor = article.uid == UserService.getCurrentUser()?.uid;
        let actualTocEnable = tocEnable && tocTree.length > 0;

        return (
            <Layout>
                <Content id="mainView">
                    <Card title={article.title} bordered={false}
                          style={{marginTop: '10px'}}>
                        <Breadcrumb items={renderBreadcrumbItems(article)}/>
                        <div style={{
                            marginTop: "10px",
                            fontSize: "12px",
                            lineHeight: "20px",
                            color: "#6a737d"
                        }}>
                            <Space size={"middle"}>
                                <IconText icon={EditTwoTone} text={"字数 " + article.wordCount}
                                          key={"word_count_" + article.aid}/>
                                <IconText icon={DashboardTwoTone}
                                          text={"创建于 " + TimeHelper.formatTimeStampToString(article.createTs)}
                                          key={"create_ts_" + article.aid}/>
                                <IconText icon={DashboardTwoTone}
                                          text={"最后修改于 " + TimeHelper.formatTimeStampToString(article.lastUpdateTs)}
                                          key={"lastUpdate_ts_" + article.aid}/>
                                <IconText icon={EyeTwoTone} text={"浏览量 " + article.pageViews}
                                          key={"page_view_" + article.aid}/>
                                {isAuthor ? <IconText icon={EyeOutlined}
                                                      text={"自浏览 " + article.selfPageViews}
                                                      key={"self_view_" + article.aid}/> : null}
                            </Space>
                        </div>
                    </Card>
                    <Card style={{marginTop: "20px"}} bordered={false}>
                        <div id={"preview"}/>
                        <ReactMarkdown className={class_md_preview}
                                       children={article.content}
                                       remarkPlugins={[remarkGfm, remarkMath]}
                                       rehypePlugins={[rehypeKatex, rehypeSlug, rehypeRaw, [rehypeHighlight, {
                                           detect: true,// 没有 language 属性的代码尝试自动解析语言类型
                                           ignoreMissing: true, // 出现故障不抛出异常打断页面渲染
                                           languages: {// 默认会装载部分语言，但手动更完整和准确
                                               bash,
                                               shell,
                                               dockerfile,
                                               nginx,
                                               javascript,
                                               typescript,
                                               java,
                                               python,
                                               sql,
                                               properties,
                                               json,
                                               xml,
                                               yaml
                                           }
                                       }]]}
                        />
                    </Card>
                </Content>
                <Sider trigger={null}
                       collapsed={!actualTocEnable}
                       width={"20%"} collapsedWidth={0}
                       style={{backgroundColor: "#F0F2F5"}}>
                    <Affix style={{
                        zIndex: 9999,
                        position: 'absolute',
                        top: 164,
                        right: 0,
                        width: "100%"
                    }} offsetTop={164}>
                        {(actualTocEnable ?
                                <div style={{paddingLeft: "20px"}}>
                                    <div className="tocTitle">目录</div>
                                    <Tree
                                        defaultExpandAll={true}
                                        showLine={true}
                                        treeData={tocTree as any}
                                        switcherIcon={<DownOutlined/>}
                                        onSelect={onSelect}
                                    >
                                    </Tree>
                                </div>
                                : <div/>
                        )}
                    </Affix>
                </Sider>
                <FloatButton.Group shape="square" style={{right: 15, zIndex: 9999}}>
                    <FloatButton onClick={() => {
                        initToc(updateTocTree, updateTocEnable, !tocEnable);
                    }}/>
                    <FloatButton.BackTop visibilityHeight={0}/>
                </FloatButton.Group>
            </Layout>

        );
    }
}


// 目录选中自动跳转函数
const onSelect: TreeProps['onSelect'] = (selectedKeys, info) => {
    // @ts-ignore
    let element = document.getElementById(info.node.value);

    if (element != null) {
        // 滚动到锚点元素的顶部
        window.scrollTo({
            top: element.offsetTop + 520,
            behavior: "smooth"
        });

        // element.scrollIntoView({behavior: "smooth", block: "start", inline: "nearest"});
    } else {
        message.warning("未找到对应跳转锚点")
    }
};

const IconText = ({icon, text}: { icon: React.FC; text: string }) => (
    <Space>
        {React.createElement(icon)}
        {text}
    </Space>
);

export default BrowserView;