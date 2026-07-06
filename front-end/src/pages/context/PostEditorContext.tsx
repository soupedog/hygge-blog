import {createContext, type ReactNode, useState} from 'react';
import {useIsMutating} from '@tanstack/react-query';
import {useSearchParams} from 'react-router-dom';
import type {ArticleDto, PostAddUpdateInput, TopicOverviewInfo} from '../../util/ApiClient.ts';
import {StorageKey} from '../../enums/EnumKeeper.ts';
import StorageHelper from '../../util/StorageHelper.ts';
import {Form, message} from 'antd';
import type {FormInstance} from 'antd/es/form/hooks/useForm';
import {useFileService, useHomeService, usePostService} from '../../util/ApiService.ts';
import PropertiesHelper from '../../util/PropertiesHelper.ts';

export interface PostEditorContextState {
    isAnyPending: boolean;
    topicInfoList: TopicOverviewInfo[];
    setTopicInfoList: Function;
    fileOptions: Array<any>,
    setFileOptions: Function;
    pid?: string;
    setPid: (input: string) => void;
    post?: ArticleDto;
    setPost: Function;
    postForm: FormInstance<PostAddUpdateInput>;
    formMode: 'query' | 'add' | 'update',
    setFormMode: (mode: 'query' | 'add' | 'update') => void;
    editorContent: string;
    setEditorContent: Function;
    backgroundMusicType: 'NONE' | 'DEFAULT' | 'WANG_YI_YUN';
    setBackgroundMusicType: Function;
    queryModalOpen: boolean;
    setQueryModalOpen: Function;
    setDraft: Function;
    removeDraft: Function;
    getDraft: Function;
    onPidChange: Function;
    fetchCategoryInfo: Function;
    fetchImageInfo: Function;
    addPost: Function;
    modifyPost: Function;
    findPost: Function;
}

export const PostEditorContext = createContext<PostEditorContextState>({} as PostEditorContextState);

export const PostEditorContextProvider = ({children}: { children: ReactNode }) => {
    // 全局的 Pending 检测，如果单独则如 signIn.isPending 即可
    const isAnyPending = useIsMutating() > 0;
    const [topicInfoList, setTopicInfoList] = useState<Array<TopicOverviewInfo>>([]);
    const [searchParams, setSearchParams] = useSearchParams();
    const [pid, setPid] = useState(searchParams.get('pid') || undefined);
    const {fetch} = useHomeService();
    const {fetchFileInfo} = useFileService();
    const {createPost, updatePost, findArticleByAid} = usePostService();

    const [post, setPost] = useState<ArticleDto | undefined>(undefined);
    const [postForm] = Form.useForm<PostAddUpdateInput>();
    const [formMode, setFormMode] = useState<'query' | 'add' | 'update'>(pid ? 'update' : 'add');
    const [editorContent, setEditorContent] = useState('');
    const [backgroundMusicType, setBackgroundMusicType] = useState<'NONE' | 'DEFAULT' | 'WANG_YI_YUN'>('NONE');

    const [queryModalOpen, setQueryModalOpen] = useState(false);

    const [fileOptions, setFileOptions] = useState<Array<any>>([]);

    const onPidChange = (nextPid?: string) => {
        // 复制一个新的 URLSearchParams 对象
        const nextParams = new URLSearchParams(searchParams);
        if (PropertiesHelper.isStringNotEmpty(nextPid)) {
            setPid(nextPid);
            nextParams.set('pid', nextPid);
        } else {
            setPid(undefined);
            nextParams.delete('pid');
        }
        setSearchParams(nextParams);
    };

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

    const fetchCategoryInfo = () => {
        fetch.mutate(undefined, {
            onSuccess: (data) => {
                setTopicInfoList(data.topicOverviewInfoList);
            }
        });
    };

    const fetchImageInfo = () => {
        fetchFileInfo.mutate({types: ['ARTICLE_COVER'], currentPage: 1, pageSize: 9999}, {
            onSuccess: (data) => {
                // @ts-ignore
                const fileInfo = [];

                data.fileInfoList.map(item => {
                    fileInfo.push(
                        {
                            value: item.fileNo,
                            label: `${item.name}——${item.fileSize}`
                        }
                    );
                });

                // @ts-ignore
                setFileOptions(fileInfo);
                message.info({content: '图片数据拉取成功！'});
            }
        });
    };

    const addPost = (input: PostAddUpdateInput) => {
        createPost.mutate(input, {
            onSuccess: (data) => {
                setPost(data);
                onPidChange(data.aid);
                // 添加完数据就可以默认接下来是修改操作了(变更相关必填参数标记)
                setFormMode('update');
                postForm.setFieldsValue({
                    action: 'update',
                    aid: data.aid
                });
                message.success({content: '创建博文成功！'});
            }
        });
    };

    const modifyPost = (input: PostAddUpdateInput) => {
        if (post) {
            updatePost.mutate(input, {
                onSuccess: (data) => {
                    setPost(data);
                    message.success({content: '修改博文成功！'});
                }
            });
        }
    };

    const findPost = () => {
        if (pid) {
            findArticleByAid.mutate(pid, {
                onSuccess: (data) => {
                    if (data) {
                        setPost(data);
                        setEditorContent(data.content);
                        setBackgroundMusicType(data.configuration.backgroundMusicType);
                        // 查完数据就可以默认接下来是修改操作了(变更相关必填参数标记)
                        setFormMode('update');
                        postForm.setFieldsValue({
                            action: 'update',
                            aid: pid,
                            orderGlobal: data.orderGlobal,
                            orderCategory: data.orderCategory,
                            cid: data.cid,
                            coverFileNo: data.coverFileNo,
                            articleState: data.articleState,
                            title: data.title,
                            summary: data.summary,
                            configuration: data.configuration
                        });
                        message.info({content: '博文数据拉取成功！'});
                    }
                }
            });
        }
    };

    return (
        <PostEditorContext value={{
            isAnyPending: isAnyPending,
            topicInfoList: topicInfoList, setTopicInfoList: setTopicInfoList,
            fileOptions: fileOptions, setFileOptions: setFileOptions,
            pid: pid, setPid: setPid,
            post: post, setPost: setPost,
            postForm: postForm,
            formMode: formMode, setFormMode: setFormMode,
            editorContent: editorContent, setEditorContent: setEditorContent,
            backgroundMusicType: backgroundMusicType, setBackgroundMusicType: setBackgroundMusicType,
            queryModalOpen: queryModalOpen, setQueryModalOpen: setQueryModalOpen,
            onPidChange: onPidChange,
            fetchCategoryInfo: fetchCategoryInfo,
            fetchImageInfo: fetchImageInfo,
            addPost: addPost, modifyPost: modifyPost, findPost: findPost,
            setDraft: setDraft, removeDraft: removeDraft, getDraft: getDraft
        }}>
            {children}
        </PostEditorContext>
    );
}
