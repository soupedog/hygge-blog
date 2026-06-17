import {useMutation} from '@tanstack/react-query';
import {HomeClient, PostClient, UserClient} from './ApiClient.ts';
import {message} from 'antd';
import {appConfiguration} from '../configuration/app.configuration.ts';

const toastZIndex = appConfiguration.toastDefaultZIndex;

// 注意：这是一个自定义 Hook 函数，不是类方法
export function useUserService() {
    const signInMutation = useMutation({
        mutationFn: UserClient.signIn,
        // 完整有 4 个参数，此处没用全
        onSuccess: (data) => {
            message.success({content: '登录成功！', duration: 2, style: {zIndex: toastZIndex}});
        }
    });

    return {
        signIn: signInMutation,
    };
}

export function usePostService() {
    const findArticleByAidMutation = useMutation({
        mutationFn: PostClient.findArticleByAid,
    });

    return {
        findArticleByAidMutation: findArticleByAidMutation
    }
}

export function useHomeService() {
    const fetchMutation = useMutation({
        mutationFn: HomeClient.fetch,
    });
    const searchPostSummaryByKeywordMutation = useMutation({
        mutationFn: HomeClient.searchPostSummaryByKeyword,
    });
    const searchQuoteByKeywordMutation = useMutation({
        mutationFn: HomeClient.searchQuoteByKeyword,
    });
    const fetchPostSummaryByTidMutation = useMutation({
        mutationFn: HomeClient.fetchPostSummaryByTid,
    });
    const fetchPostSummaryByCidMutation = useMutation({
        mutationFn: HomeClient.fetchPostSummaryByCid,
    });

    return {
        fetch: fetchMutation,
        searchPostSummaryByKeyword: searchPostSummaryByKeywordMutation,
        searchQuoteByKeyword: searchQuoteByKeywordMutation,
        fetchPostSummaryByTid: fetchPostSummaryByTidMutation,
        fetchPostSummaryByCid: fetchPostSummaryByCidMutation,
    }
}