import * as React from 'react';
import {useEffect, useState} from 'react';
import {useParams} from 'react-router-dom';

import './PostBrowser.css'

import {FloatButton, Layout, message, Splitter, Tooltip} from 'antd';
import {appConfiguration} from '../configuration/app.configuration.ts';
import {usePostService} from '../util/ApiService.ts';
import {type ArticleDto} from '../util/ApiClient.ts';
import UrlHelper from '../util/UrlHelper.ts';
import AppFooter from './component/AppFooter.tsx';
import {type AntdTreeNodeInfo, type CreateTocTreeInputParam, MdHelper, type TreeNodeInfo} from '../util/markdown/MdHelper.ts';
import PostBrowserHeader from './component/PostBrowserHeader.tsx';
import PostBrowserMusicPlayer from './component/PostBrowserMusicPlayer.tsx';
import PostBrowserContentView from './component/PostBrowserContentView.tsx';
import PostBrowserTocView from './component/PostBrowserTocView.tsx';

const toastZIndex = appConfiguration.toastDefaultZIndex;

export default function PostBrowser() {
    const [post, setPost] = useState<ArticleDto | undefined>(undefined);
    const [tocEnable, setTocEnable] = useState<boolean>(false);
    const [tocTree, setTocTree] = useState<Array<TreeNodeInfo>>([]);
    const {pid} = useParams();
    const {findArticleByAidMutation} = usePostService();

    useEffect(() => {
        findArticleByAidMutation.mutate(pid!, {
            onSuccess: (data) => {
                if (data) {
                    // 依赖静态值表示仅初始化时调用一次
                    document.title = `${data.title} | 我的小宅子`;
                    setPost(data);
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

    return (
        <Layout>
            <PostBrowserHeader isAnyPending={false}/>
            <div className={'PostBrowser_image'} style={{
                width: '100%',
                height: '25rem',
                background: 'url(' + post.imageSrc + ') no-repeat center / cover'
            }}/>
            <PostBrowserMusicPlayer configuration={post.configuration}/>
            <Layout style={{marginBottom: '2rem'}}>
                {tocEnable ?
                    <Splitter style={{boxShadow: '0 0 10px rgba(0, 0, 0, 0.1)'}}>
                        <Splitter.Panel>
                            <PostBrowserContentView post={post} key={'post_browser_content'}/>
                        </Splitter.Panel>
                        <Splitter.Panel defaultSize='20%' min='20%' max='50%'>
                            <PostBrowserTocView tocTree={tocTree} key={'post_browser_toc'}/>
                        </Splitter.Panel>
                    </Splitter> : <PostBrowserContentView post={post} key={'post_browser_content'}/>
                }
                <FloatButton.Group shape='square' style={{zIndex: toastZIndex}}>
                    <Tooltip placement='left' title={'目录'}>
                        <FloatButton onClick={() => {
                            if (tocTree.length > 0) {
                                setTocEnable(!tocEnable);
                            } else {
                                message.info('未找到目录结构');
                            }
                        }}/>
                    </Tooltip>
                    <Tooltip placement='left' title={'回到顶部'}>
                        <FloatButton.BackTop visibilityHeight={0}/>
                    </Tooltip>
                </FloatButton.Group>
            </Layout>
            <AppFooter/>
        </Layout>
    );

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
            setTocTree(currentTOC);
            setTocEnable(true);
        } else {
            message.info('未找到目录结构');
        }
    }
}
