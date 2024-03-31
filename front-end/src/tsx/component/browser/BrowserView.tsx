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
import {AntdTreeNodeInfo, CreateTocTreeInputParam, MdHelper, TreeNodeInfo} from "../markdown/util/MdHelper";
import {TimeHelper} from "../../util/UtilContainer";
import {MdPreview} from "md-editor-rt";
import {allowAll, editor_id_for_browser} from "../markdown/properties/MarkDownStaticValue";

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
    let antdTreeNodeInfos = new Array<TreeNodeInfo>();
    let map = new Map<number, TreeNodeInfo>();

    document.querySelectorAll('H1[data-line][id], H2[data-line][id], H3[data-line][id], H4[data-line][id], H5[data-line][id], H6[data-line][id]').forEach((item, index) => {
        // @ts-ignore
        let antdTreeNode: TreeNodeInfo = {
            title: item.textContent!,
            children: new Array<AntdTreeNodeInfo>(),
            index: index,
            id: item.id,
            dataLine: item.getAttribute('data-line')!,
            nodeName: item.tagName,
        };

        antdTreeNodeInfos.push(antdTreeNode);
        map.set(index, antdTreeNode);
    });

    let currentTOC = MdHelper.initTitleTree({
        currentTOCArray: antdTreeNodeInfos,
        allTocNodeMap: map
    } as CreateTocTreeInputParam);

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
                        <MdPreview editorId={editor_id_for_browser} modelValue={article.content} sanitize={allowAll}/>
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
    let item: TreeNodeInfo = info.node;

    // 用标签类型 + data-line 属性做筛选
    let element = document.querySelector(item.nodeName + '[data-line="' + item.dataLine + '"]');

    if (element != undefined) {
        // 滚动到锚点元素的顶部(offsetTop 是数字类型，你可以在此基础上追加偏移量)

        window.scrollTo({
            // @ts-ignore
            top: element.offsetTop + 520,
            behavior: "smooth"
        });

        // 拿到 dom 元素可以直接使用此方法滚动到目标位置(无法追加偏移量)
        // element.scrollIntoView({behavior: "smooth", block: "start", inline: "nearest"});
    } else {
        message.warning("未找到对应跳转锚点");
    }
};

const IconText = ({icon, text}: { icon: React.FC; text: string }) => (
    <Space>
        {React.createElement(icon)}
        {text}
    </Space>
);

export default BrowserView;