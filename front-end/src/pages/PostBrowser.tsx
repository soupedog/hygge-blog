import {useEffect, useState} from 'react';
import {useParams} from 'react-router-dom';
import {usePostService} from '../util/ApiService.ts';
import {message} from 'antd';
import type {ArticleDto} from '../util/ApiClient.ts';
import {appConfiguration} from '../configuration/app.configuration.ts';
import UrlHelper from '../util/UrlHelper.ts';

const toastZIndex = appConfiguration.toastDefaultZIndex;

export default function PostBrowser() {
    const [post, updatePost] = useState<ArticleDto | undefined>(undefined);
    const {pid} = useParams();
    const {findArticleByAidMutation} = usePostService();

    useEffect(() => {
        findArticleByAidMutation.mutate(pid!, {
            onSuccess: (data) => {
                if (data) {
                    updatePost(data);
                } else {
                    message.warning({content: '目标文章不存在，2 秒内自动跳转回主页，请稍后', duration: 2, style: {zIndex: toastZIndex}});
                    UrlHelper.navigateTo({path: '/', delayTime: 2000, canBack: false});
                }
            }
        });
    }, [pid]);

    return (
        <>
            {post?.content}
        </>
    );
}
