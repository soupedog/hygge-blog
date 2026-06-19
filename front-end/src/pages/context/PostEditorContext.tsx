import {createContext, type ReactNode, useState} from 'react';
import {useIsMutating} from '@tanstack/react-query';
import {useSearchParams} from 'react-router-dom';
import type {ArticleDto} from '../../util/ApiClient.ts';
import {StorageKey} from '../../enums/EnumKeeper.ts';
import StorageHelper from '../../util/StorageHelper.ts';

export interface PostEditorContextState {
    pid?: string;
    setPid: (input: string) => void;
    post?: ArticleDto;
    setPost: Function;
    editorContent: string;
    setEditorContent: Function;
    setDraft: Function;
    removeDraft: Function;
    getDraft: Function;
}

export const PostEditorContext = createContext<PostEditorContextState>({} as PostEditorContextState);

export const PostEditorContextProvider = ({children}: { children: ReactNode }) => {
    // 全局的 Pending 检测，如果单独则如 signIn.isPending 即可
    const isAnyPending = useIsMutating() > 0;
    const [searchParams, setSearchParams] = useSearchParams();
    const [post, setPost] = useState<ArticleDto | undefined>(undefined);
    const [editorContent, setEditorContent] = useState('');

    const [fileOptions, setFileOptions] = useState<Array<any>>([]);

    const [pid, setPid] = useState(searchParams.get('pid') || undefined);

    const getDraft = (pid?: string) => {
        const actualPid = pid ? pid : '';
        return StorageHelper.get<string>(StorageKey.DRAFT_PREFIX + actualPid);
    };

    const setDraft = (content: string, pid?: string) => {
        const actualPid = pid ? pid : '';
        StorageHelper.set(StorageKey.DRAFT_PREFIX + actualPid, content);
    };

    const removeDraft = (pid?: string) => {
        const actualPid = pid ? pid : '';
        StorageHelper.remove(StorageKey.DRAFT_PREFIX + actualPid);
    }

    return (
        <PostEditorContext value={{
            pid: pid, setPid: setPid,
            post: post, setPost: setPost,
            editorContent: editorContent, setEditorContent: setEditorContent,
            setDraft: setDraft, removeDraft: removeDraft, getDraft: getDraft,
        }}>
            {children}
        </PostEditorContext>
    );
}
