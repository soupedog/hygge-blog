import * as React from 'react';
import {useEffect} from 'react';
import {type QuoteDto, UserClient} from '../../util/ApiClient.ts';
import {List, Space, Tooltip} from 'antd';
import PropertiesHelper from '../../util/PropertiesHelper.ts';
import {FormOutlined} from '@ant-design/icons';
import UrlHelper from '../../util/UrlHelper.ts';
import {MdPreview} from 'md-editor-rt';

export interface HomeTabListQuoteItemProps {
    readonly quote: QuoteDto;
}

const EditIcon = ({icon, text, quoteId}: { icon: React.FC; text: string, quoteId: number }) => (
    <Space className={'pointer'}
           onClick={() => {
               UrlHelper.navigateTo({path: `/manage/editor/quote?quoteId=${quoteId}`});
           }}
           style={{
               float: 'right',
               marginRight: '20px',
               fontSize: '14px'
           }}>
        {React.createElement(icon)}
        {text}
    </Space>
);

export default function HomeTabQuoteListItem({quote}: HomeTabListQuoteItemProps) {
    const currentUser = UserClient.getCurrentUser();
    const isAuthor: boolean = currentUser != null && currentUser.uid == quote.uid;

    useEffect(() => {
        // 依赖静态值表示仅初始化时调用一次
    }, []);

    return (
        <List.Item
            extra={PropertiesHelper.isStringNotEmpty(quote.imageSrc) ? <img width={272} alt='quateLogo' src={quote.imageSrc}/> : null}
        >
            <List.Item.Meta
                title={
                    quote.source == null ? null :
                        <>
                            <Tooltip placement='right' title={'可能的出处'}>
                                <span style={{fontSize: '24px', fontWeight: 'bold'}}>{quote.source}</span>
                            </Tooltip>
                            {
                                isAuthor ? <EditIcon icon={FormOutlined} text={'编辑'}
                                                     quoteId={quote.quoteId}/> : null
                            }
                        </>
                }
                description={
                    quote.portal == null ? null :
                        <>
                                <span
                                    style={{
                                        fontSize: '14px',
                                        color: '#0039f6',
                                        fontWeight: 'bold'
                                    }}>&emsp;传送门:&emsp;</span>
                            <a href={quote.portal}
                               target='_blank'>{quote.portal}</a>
                        </>
                }
            />
            <div className={'md-preview'}>
                <MdPreview
                    id={`editor_id_for_browser_${quote.quoteId}`} value={quote.content}
                    sanitize={(html) => html}/>
            </div>
        </List.Item>
    );
}
