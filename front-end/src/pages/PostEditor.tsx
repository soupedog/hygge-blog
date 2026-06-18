import {useEffect} from 'react';
import {Layout, Modal} from 'antd';
import AppBaseHeader from './component/AppBaseHeader.tsx';
import {Content} from 'antd/es/layout/layout';
import AppFooter from './component/AppFooter.tsx';
import PostEditorForm from './component/PostEditorForm.tsx';

export default function PostEditor() {

    useEffect(() => {
        // 依赖静态值表示仅初始化时调用一次
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
                {/*<QuoteEditorForm/>*/}
                <PostEditorForm/>
            </Content>
            <AppFooter/>
        </Layout>
    );
}
