import {useContext, useEffect} from 'react';
import {Layout, message, Modal} from 'antd';
import AppBaseHeader from './component/AppBaseHeader.tsx';
import {Content} from 'antd/es/layout/layout';
import AppFooter from './component/AppFooter.tsx';
import PostEditorForm from './component/PostEditorForm.tsx';
import {PostEditorContext} from './context/PostEditorContext.tsx';
import {usePostService} from '../util/ApiService.ts';
import PostMarkdownEditor from './component/PostMarkdownEditor.tsx';

export default function PostEditor() {
    const {
        pid,
        setEditorContent,
        post, setPost,
    } = useContext(PostEditorContext);

    const {findArticleByAid} = usePostService();

    useEffect(() => {
        // 依赖静态值表示仅初始化时调用一次
        document.title = `博文编辑 | 我的小宅子`;

        // 依赖静态值表示仅初始化时调用一次
        if (pid) {
            findArticleByAid.mutate(pid, {
                onSuccess: (data) => {
                    if (data) {
                        setPost(data);
                        setEditorContent(data.content);
                        message.info({content: '博文数据拉取成功！'});
                    }
                }
            });
        }
    }, []);

    return (
        <Layout className={'full-screen-min-y'}>
            <AppBaseHeader title={'博文编辑'} isAnyPending={true}/>
            <Content>
                <Modal key={'postQueryModal'}
                       title='请注意'
                       open={undefined}
                       onOk={(event) => {
                           // getQuoteByQuoteId();
                       }}
                       confirmLoading={true}
                       onCancel={(event) => {
                           // setQueryModalOpen(false);
                       }}
                >
                    <p>执行查询将丢失编辑数据</p>
                </Modal>
                <PostMarkdownEditor/>
                <PostEditorForm/>
            </Content>
            <AppFooter/>
        </Layout>
    );
}
