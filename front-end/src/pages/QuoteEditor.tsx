import {useContext, useEffect} from 'react';
import {Layout, Modal} from 'antd';
import AppBaseHeader from './component/AppBaseHeader.tsx';
import AppFooter from './component/AppFooter.tsx';
import {Content} from 'antd/es/layout/layout';
import QuoteEditorForm from './component/QuoteEditorForm.tsx';
import QuoteEditorPreview from './component/QuoteEditorPreview.tsx';
import {QuoteEditorContext} from './context/QuoteEditorContext.tsx';

export default function QuoteEditor() {
    const {
        isAnyPending,
        quoteId,
        queryModalOpen,
        setQueryModalOpen,
        getQuoteByQuoteId,
    } = useContext(QuoteEditorContext);

    useEffect(() => {
        // 初次加载页面


        if (quoteId) {
            getQuoteByQuoteId();
        }

    }, []);

    return (
        <Layout className={'full-screen-min-y'}>
            <AppBaseHeader title={'句子收藏编辑'} isAnyPending={isAnyPending}/>
            <Content>
                <Modal key={'quoteQueryModal'}
                       title='请注意'
                       open={queryModalOpen}
                       onOk={(event) => {
                           getQuoteByQuoteId();
                       }}
                       confirmLoading={isAnyPending}
                       onCancel={(event) => {
                           setQueryModalOpen(false);
                       }}
                >
                    <p>执行查询将丢失编辑数据</p>
                </Modal>
                <QuoteEditorForm/>
                <QuoteEditorPreview/>
            </Content>
            <AppFooter/>
        </Layout>
    );
}
