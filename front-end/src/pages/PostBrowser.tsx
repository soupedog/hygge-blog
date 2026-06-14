import * as React from 'react';
import {useEffect, useState} from 'react';
import {useParams} from 'react-router-dom';

import './PostBrowser.css'

import {usePostService} from '../util/ApiService.ts';
import {Breadcrumb, Card, FloatButton, Layout, message, Space, Tree, type TreeProps} from 'antd';
import {DashboardTwoTone, DownOutlined, EditTwoTone, EyeOutlined, EyeTwoTone} from '@ant-design/icons';
import {type ArticleDto, UserClient} from '../util/ApiClient.ts';
import {appConfiguration} from '../configuration/app.configuration.ts';
import UrlHelper from '../util/UrlHelper.ts';
import AppFooter from './component/AppFooter.tsx';
import {Content} from 'antd/es/layout/layout';
import Sider from 'antd/es/layout/Sider';
import {TimeHelper} from '../util/TimeHelper.ts';
import {TimeType} from '../enums/EnumKeeper.ts';
import {MdPreview} from 'md-editor-rt';
import {type AntdTreeNodeInfo, type CreateTocTreeInputParam, MdHelper, type TreeNodeInfo} from '../util/markdown/MdHelper.ts';
import PostBrowserHeader from './component/PostBrowserHeader.tsx';

const toastZIndex = appConfiguration.toastDefaultZIndex;
const IconText = ({icon, text}: { icon: React.FC; text: string }) => (
    <Space>
        {React.createElement(icon)}
        {text}
    </Space>
);

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
            top: element.offsetTop + 540,
            behavior: 'smooth'
        });

        // 拿到 dom 元素可以直接使用此方法滚动到目标位置(无法追加偏移量)
        // element.scrollIntoView({behavior: "smooth", block: "start", inline: "nearest"});
    } else {
        message.warning('未找到对应跳转锚点');
    }
};

export default function PostBrowser() {
    const [post, updatePost] = useState<ArticleDto | undefined>(undefined);
    const [tocEnable, updateTocEnable] = useState<boolean>(false);
    const [tocTree, updateTocTree] = useState<Array<TreeNodeInfo>>([]);
    const {pid} = useParams();
    const {findArticleByAidMutation} = usePostService();

    useEffect(() => {
        findArticleByAidMutation.mutate(pid!, {
            onSuccess: (data) => {
                if (data) {
                    // 依赖静态值表示仅初始化时调用一次
                    document.title = `${data.title} | 我的小宅子`;
                    updatePost(data);

                    // 需要等待 markdown Html 元素渲染完成
                    window.setTimeout(() => {
                        initToc();
                    }, 1000);

                } else {
                    message.warning({content: '目标文章不存在，2 秒内自动跳转回主页，请稍后……', duration: 2, style: {zIndex: toastZIndex}});
                    UrlHelper.navigateTo({path: '/', delayTime: 2000, canBack: false});
                }
            }
        });
    }, [pid]);

    // 博客不存在时无需渲染
    if (post == null) {
        return null;
    }

    const currentUser = UserClient.getCurrentUser();
    const isAuthor: boolean = currentUser != null && currentUser.uid == post.uid;

    return (
        <Layout>
            <PostBrowserHeader title={'测试'} isAnyPending={false}/>
            <div className={'PostBrowser_image'} style={{
                width: '100%',
                height: '25rem',
                background: 'url(' + post.imageSrc + ') no-repeat center / cover'
            }}/>
            <Layout>
                <Sider style={{backgroundColor: '#F0F2F5', paddingTop: '12rem'}} width='20%' collapsedWidth={0} collapsed={!tocEnable}>
                    {tocEnable ? <Card variant={'borderless'} styles={{body: {padding: 8}}}
                                       style={{
                                           marginRight: '0.5rem',
                                           position: 'sticky',
                                           top: '12rem'
                                       }}
                                       className={'postBrowserToc'}>
                        <div className='tocTitle'>目录
                        </div>
                        <Tree
                            defaultExpandAll={true}
                            showLine={true}
                            treeData={tocTree as any}
                            switcherIcon={<DownOutlined/>}
                            onSelect={onSelect}
                        >
                        </Tree>
                    </Card> : null}
                </Sider>
                <Content>
                    <Card title={post.title} variant={'borderless'} style={{
                        marginTop: '1rem'
                    }}>
                        <Breadcrumb items={renderBreadcrumbItems(post)}/>
                        <div style={{
                            marginTop: '1rem',
                            fontSize: '0.8rem',
                            lineHeight: '1.6rem',
                            color: '#6a737d'
                        }}>
                            <Space size={'middle'}>
                                <IconText icon={EditTwoTone} text={'字数 ' + post.wordCount}
                                          key={'word_count_' + post.aid}/>
                                <IconText icon={DashboardTwoTone}
                                          text={'创建于 ' + TimeHelper.formatTimeStampToString(post.createTs, TimeType.yyyy_mm_dd)}
                                          key={'create_ts_' + post.aid}/>
                                <IconText icon={DashboardTwoTone}
                                          text={'最后修改于 ' + TimeHelper.formatTimeStampToString(post.lastUpdateTs, TimeType.yyyy_mm_dd)}
                                          key={'lastUpdate_ts_' + post.aid}/>
                                <IconText icon={EyeTwoTone} text={'浏览量 ' + post.pageViews}
                                          key={'page_view_' + post.aid}/>
                                {isAuthor ? <IconText icon={EyeOutlined} text={'自浏览 ' + post.selfPageViews}
                                                      key={'self_view_' + post.aid}/>
                                    : null
                                }
                            </Space>
                        </div>
                    </Card>
                    <Card style={{marginTop: '20px'}} variant={'borderless'}>
                        <MdPreview value={post.content} sanitize={(html) => html}/>
                    </Card>
                </Content>
                <FloatButton.Group shape='square' style={{zIndex: 20001}}>
                    <FloatButton onClick={() => {
                        updateTocEnable(!tocEnable);
                    }}/>
                    <FloatButton.BackTop visibilityHeight={0}/>
                </FloatButton.Group>
            </Layout>
            <AppFooter/>
        </Layout>
    );

    function renderBreadcrumbItems(article: ArticleDto) {
        let result = [];
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
        return result;
    }

    function initToc() {
        let antdTreeNodeInfos = new Array<TreeNodeInfo>();
        let map = new Map<number, TreeNodeInfo>();

        document.querySelectorAll('H1[data-line][id], H2[data-line][id], H3[data-line][id], H4[data-line][id], H5[data-line][id], H6[data-line][id]').forEach((item, index) => {
            let antdTreeNode: TreeNodeInfo = {
                title: item.textContent,
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
            updateTocEnable(true);
        } else {
            message.info('未找到目录结构');
        }
    }
}
