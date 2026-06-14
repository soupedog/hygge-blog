import * as React from 'react';
import {useEffect, useState} from 'react';
import {useParams} from 'react-router-dom';

import './PostBrowser.css'

import {usePostService} from '../util/ApiService.ts';
import {Breadcrumb, Card, Layout, message, Space} from 'antd';
import {DashboardTwoTone, EditTwoTone, EyeOutlined, EyeTwoTone} from '@ant-design/icons';
import {type ArticleDto, UserClient} from '../util/ApiClient.ts';
import {appConfiguration} from '../configuration/app.configuration.ts';
import UrlHelper from '../util/UrlHelper.ts';
import AppFooter from './component/AppFooter.tsx';
import {Content} from 'antd/es/layout/layout';
import Sider from 'antd/es/layout/Sider';
import SimpleHeader from './component/SimpleHeader.tsx';
import {TimeHelper} from '../util/TimeHelper.ts';
import {TimeType} from '../enums/EnumKeeper.ts';
import {MdPreview} from 'md-editor-rt';

const toastZIndex = appConfiguration.toastDefaultZIndex;

const contentStyle: React.CSSProperties = {
    // textAlign: 'center',
    // minHeight: '666px',
    // lineHeight: '120px',
    // color: '#fff',
    // backgroundColor: '#0958d9',
};

const siderStyle: React.CSSProperties = {
    // textAlign: 'center',
    // lineHeight: '120px',
    color: '#fff',
    backgroundColor: '#1677ff',
};

const IconText = ({icon, text}: { icon: React.FC; text: string }) => (
    <Space>
        {React.createElement(icon)}
        {text}
    </Space>
);

export default function PostBrowser() {
    const [post, updatePost] = useState<ArticleDto | undefined>(undefined);
    const {pid} = useParams();
    const {findArticleByAidMutation} = usePostService();

    useEffect(() => {
        findArticleByAidMutation.mutate(pid!, {
            onSuccess: (data) => {
                if (data) {
                    // 依赖静态值表示仅初始化时调用一次
                    document.title = `${data.title} | 我的小宅子`;
                    updatePost(data);
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
            <SimpleHeader title={'测试'} isAnyPending={false}/>
            <div className={'PostBrowser_image'} style={{
                width: '100%',
                height: '25rem',
                background: 'url(' + post.imageSrc + ') no-repeat center / cover'
            }}/>
            <Layout>
                <Sider width='20%' style={siderStyle} collapsed={false}>
                    Sider
                </Sider>
                <Content style={contentStyle}>
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
}
