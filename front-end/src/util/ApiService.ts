import {useMutation} from '@tanstack/react-query';
import {FileClient, HomeClient, PostClient, QuoteClient, UserClient} from './ApiClient.ts';
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
        findArticleByAid: findArticleByAidMutation
    }
}

export function useQuoteService() {
    const createQuoteMutation = useMutation({
        mutationFn: QuoteClient.createQuote,
    });
    const updateQuoteMutation = useMutation({
        mutationFn: QuoteClient.updateQuote,
    });
    const findQuoteMutation = useMutation({
        mutationFn: QuoteClient.findQuote,
    });

    return {
        createQuote: createQuoteMutation,
        updateQuote: updateQuoteMutation,
        findQuote: findQuoteMutation
    }
}

export function useFileCService() {
    const fetchFileInfoMutation = useMutation({
        mutationFn: FileClient.fetchFileInfo,
    });

    return {
        fetchFileInfo: fetchFileInfoMutation
    }
}

export function useHomeService() {
    const fetchMutation = useMutation({
        mutationFn: HomeClient.fetch,
    });
    const fetchPostSummaryByTidMutation = useMutation({
        mutationFn: HomeClient.fetchPostSummaryByTid,
    });
    const fetchPostSummaryByCidMutation = useMutation({
        mutationFn: HomeClient.fetchPostSummaryByCid,
    });
    const fetchQuoteMutation = useMutation({
        mutationFn: HomeClient.fetchQuote,
    });
    const searchPostSummaryByKeywordMutation = useMutation({
        mutationFn: HomeClient.searchPostSummaryByKeyword,
    });
    const searchQuoteByKeywordMutation = useMutation({
        mutationFn: HomeClient.searchQuoteByKeyword,
    });

    return {
        fetch: fetchMutation,
        fetchPostSummaryByTid: fetchPostSummaryByTidMutation,
        fetchPostSummaryByCid: fetchPostSummaryByCidMutation,
        fetchQuote: fetchQuoteMutation,
        searchPostSummaryByKeyword: searchPostSummaryByKeywordMutation,
        searchQuoteByKeyword: searchQuoteByKeywordMutation,
    }
}