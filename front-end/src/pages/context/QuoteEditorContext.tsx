import {createContext, type ReactNode, useState} from 'react';
import {useIsMutating} from '@tanstack/react-query';
import {Form, message} from 'antd';
import {useSearchParams} from 'react-router-dom';
import {useFileCService, useQuoteService} from '../../util/ApiService.ts';
import type {QuoteDto} from '../../util/ApiClient.ts';
import type {FormInstance} from 'antd/es/form/hooks/useForm';
import PropertiesHelper from '../../util/PropertiesHelper.ts';
import type {QuoteFO} from '../component/QuoteEditorForm.tsx';

export interface QuoteEditorContextState {
    isAnyPending: boolean;
    searchParams: URLSearchParams;
    setSearchParams: (input: URLSearchParams) => void;
    quoteForm: FormInstance;
    quoteId?: string;
    setQuoteId: Function;
    quote?: QuoteDto;
    setQuote: (input: QuoteDto) => void;
    queryModalOpen: boolean;
    setQueryModalOpen: Function;
    onQuoteIdChange: Function;
    fetchImageInfo: Function;
    getQuoteByQuoteId: Function;
}

export const QuoteEditorContext = createContext<QuoteEditorContextState>({} as QuoteEditorContextState);

export const QuoteEditorProvider = ({children}: { children: ReactNode }) => {
    // 全局的 Pending 检测，如果单独则如 signIn.isPending 即可
    const isAnyPending = useIsMutating() > 0;
    const [searchParams, setSearchParams] = useSearchParams();
    const [quoteId, setQuoteId] = useState(searchParams.get('quoteId') || undefined);

    const [quoteForm] = Form.useForm<QuoteFO>();
    const [quote, setQuote] = useState<QuoteDto | undefined>(undefined);
    const [queryModalOpen, setQueryModalOpen] = useState(false);

    const {findQuote} = useQuoteService();
    const {fetchFileInfo} = useFileCService();

    const onQuoteIdChange = (nextQuoteId?: string) => {
        // 复制一个新的 URLSearchParams 对象
        const nextParams = new URLSearchParams(searchParams);
        if (PropertiesHelper.isStringNotEmpty(nextQuoteId)) {
            setQuoteId(nextQuoteId);
            nextParams.set('quoteId', nextQuoteId);
        } else {
            setQuoteId(undefined);
            nextParams.delete('quoteId');
        }
        setSearchParams(nextParams);
    };

    const fetchImageInfo = () => {
        fetchFileInfo.mutate({fileType: 'QUOTE'}, {
            onSuccess: (data) => {

            }
        });
    };

    const getQuoteByQuoteId = () => {
        if (quoteId) {
            findQuote.mutate({quoteId: quoteId}, {
                onSuccess: (data) => {
                    setQueryModalOpen(false);
                    setQuote(data);
                    quoteForm.setFieldsValue({
                        quoteId: String(data.quoteId),
                        coverFileNo: data.coverFileNo,
                        content: data.content,
                        remarks: data.remarks,
                        source: data.source,
                        portal: data.portal,
                        quoteState: data.quoteState,
                        orderVal: data.orderVal,
                    });

                    message.success('拉取句子收藏成功！');
                }
            });
        }
    };

    return (
        <QuoteEditorContext value={{
            isAnyPending: isAnyPending,
            searchParams: searchParams, setSearchParams: setSearchParams,
            quoteForm: quoteForm,
            quoteId: quoteId, setQuoteId: setQuoteId,
            quote: quote, setQuote: setQuote,
            queryModalOpen: queryModalOpen, setQueryModalOpen: setQueryModalOpen,
            onQuoteIdChange: onQuoteIdChange,
            fetchImageInfo: fetchImageInfo,
            getQuoteByQuoteId: getQuoteByQuoteId,
        }}>
            {children}
        </QuoteEditorContext>
    );
}
