import * as React from 'react';
import {Image, List, Space} from 'antd';
import {type ArticleDto, type CategoryDto, UserClient} from '../../util/ApiClient.ts';
import {DashboardTwoTone, EditTwoTone, EyeOutlined, EyeTwoTone, FormOutlined} from '@ant-design/icons';
import {TimeHelper} from '../../util/TimeHelper.ts';
import {TimeType} from '../../enums/EnumKeeper.ts';
import clsx from 'clsx';
import UrlHelper from '../../util/UrlHelper.ts';

export interface HomePostListItemProps {
    readonly post: ArticleDto;
}

const IconText = ({icon, text}: { icon: React.FC; text: string }) => (
    <Space>
        {React.createElement(icon)}
        {text}
    </Space>
);

const EditIcon = ({icon, text, pid}: { icon: React.FC; text: string, pid: string }) => (
    <Space className={'pointer'}
           onClick={() => {
               UrlHelper.navigateTo({path: `/manage/editor/post?pid=${pid}`});
           }}
           style={{
               float: 'right',
               marginRight: '2rem',
               fontSize: '1rem'
           }}>
        {React.createElement(icon)}
        {text}
    </Space>
);

function getCategoryInfo(articleSummary: ArticleDto): string {
    let result = articleSummary.categoryTreeInfo.topicInfo.topicName;

    articleSummary.categoryTreeInfo.categoryList.forEach((item: CategoryDto) => {
        result = result + ' / ' + item.categoryName
    });
    return result;
}

export default function HomeTabPostListItem({post}: HomePostListItemProps) {
    const currentUser = UserClient.getCurrentUser();
    const isAuthor: boolean = currentUser != null && currentUser.uid == post.uid;
    let isDraft = post.articleState == 'DRAFT';

    const actionItems = [
        <IconText icon={EditTwoTone} text={'字数 ' + post.wordCount}/>,
        <IconText icon={DashboardTwoTone} text={TimeHelper.formatTimeStampToString(post.createTs, TimeType.yyyy_mm_dd)}/>,
        <IconText icon={EyeTwoTone} text={'浏览量 ' + post.pageViews}/>,
    ];

    if (isAuthor) {
        actionItems.push(
            <IconText icon={EyeOutlined} text={'自浏览 ' + post.selfPageViews}/>
        );
    }

    return (
        <List.Item
            actions={actionItems}
            extra={
                <Image
                    width={272}
                    height={153}
                    alt='postImage'
                    src={post.imageSrc}
                    preview={false}
                    style={{
                        objectFit: 'contain',  // 保持比例，不拉伸
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center'
                    }}
                />
            }
        >
            <List.Item.Meta
                title={
                    <>
                        <a className={clsx({'draftHighlight': isDraft})}
                           style={{fontSize: '32px', fontWeight: 900, lineHeight: '40px'}}
                           href={UrlHelper.getHomePagePrefix() + '/post/' + post.aid}
                           target='_blank'>{post.title}{isDraft ? '【草稿】' : null}
                        </a>
                        {
                            isAuthor ? <EditIcon icon={FormOutlined} text={'编辑'} pid={post.aid}/> : null
                        }
                    </>
                }
                description={getCategoryInfo(post)}
            />
            <div style={{textIndent: '2em', fontSize: '14px', lineHeight: '24px'}}>
                {post.summary}
            </div>
        </List.Item>
    );
}
