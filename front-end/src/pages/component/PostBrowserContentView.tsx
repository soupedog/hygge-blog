import * as React from 'react';
import {useEffect} from 'react';
import {Content} from 'antd/es/layout/layout';
import {Breadcrumb, Card, Space} from 'antd';
import {DashboardTwoTone, EditTwoTone, EyeOutlined, EyeTwoTone} from '@ant-design/icons';
import {TimeHelper} from '../../util/TimeHelper.ts';
import {TimeType} from '../../enums/EnumKeeper.ts';
import {MdPreview} from 'md-editor-rt';
import {type ArticleDto, UserClient} from '../../util/ApiClient.ts';

export interface PostBrowserContentViewProps {
    readonly post: ArticleDto;
}

const IconText = ({icon, text}: { icon: React.FC; text: string }) => (
    <Space>
        {React.createElement(icon)}
        {text}
    </Space>
);

export default function PostBrowserContentView({post}: PostBrowserContentViewProps) {
    const currentUser = UserClient.getCurrentUser();
    const isAuthor: boolean = currentUser != null && currentUser.uid == post.uid;

    useEffect(() => {
        // 依赖静态值表示仅初始化时调用一次
    }, []);

    return (
        <Content>
            <Card title={post.title} variant={'borderless'} style={{
                marginTop: '1rem'
            }}>
                <Breadcrumb items={buildBreadcrumbItems(post)}/>
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
            <Card style={{marginTop: '1rem'}} variant={'borderless'}>
                <MdPreview value={post.content} sanitize={(html) => html}/>
            </Card>
        </Content>
    );

    function buildBreadcrumbItems(article: ArticleDto) {
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
