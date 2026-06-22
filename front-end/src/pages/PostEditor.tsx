import {useContext, useEffect} from 'react';
import {Layout, message, Modal} from 'antd';
import AppBaseHeader from './component/AppBaseHeader.tsx';
import {Content} from 'antd/es/layout/layout';
import AppFooter from './component/AppFooter.tsx';
import PostEditorForm from './component/PostEditorForm.tsx';
import {PostEditorContext} from './context/PostEditorContext.tsx';
import PostMarkdownEditor from './component/PostMarkdownEditor.tsx';
import PropertiesHelper from '../util/PropertiesHelper.ts';

export default function PostEditor() {
    const {
        isAnyPending,
        pid,
        postForm,
        queryModalOpen, setQueryModalOpen,
        setEditorContent,
        setBackgroundMusicType,
        getDraft,
        post, setPost,
        fetchCategoryInfo,
        fetchImageInfo,
        findPost,
    } = useContext(PostEditorContext);

    useEffect(() => {
        // 依赖静态值表示仅初始化时调用一次
        document.title = `博文编辑 | 我的小宅子`;

        const draft = getDraft(pid);
        if (PropertiesHelper.isStringNotEmpty(draft)) {
            setEditorContent(draft);
            message.info({content: '已从本地草稿中恢复数据！'});
        } else {
            fetchCategoryInfo();
            fetchImageInfo();
            findPost();
        }
        // 依赖静态值表示仅初始化时调用一次
    }, []);

    return (
        <Layout className={'full-screen-min-y'}>
            <AppBaseHeader title={'博文编辑'} isAnyPending={isAnyPending}/>
            <Content>
                <Modal key={'postQueryModal'}
                       title='请注意'
                       open={queryModalOpen}
                       onOk={(event) => {
                           findPost();
                           setQueryModalOpen(false);
                       }}
                       confirmLoading={isAnyPending}
                       onCancel={(event) => {
                           setQueryModalOpen(false);
                       }}
                >
                    <p>执行查询将丢失正在编辑的数据！</p>
                </Modal>
                <PostMarkdownEditor/>
                <PostEditorForm/>
            </Content>
            <AppFooter/>
        </Layout>
    );
}
