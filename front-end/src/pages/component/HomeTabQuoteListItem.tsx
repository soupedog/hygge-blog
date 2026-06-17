import * as React from 'react';
import {type QuoteDto, UserClient} from '../../util/ApiClient.ts';
import {Image, List, Space, Tooltip} from 'antd';
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
               marginRight: '2rem',
               fontSize: '1rem'
           }}>
        {React.createElement(icon)}
        {text}
    </Space>
);

export default function HomeTabQuoteListItem({quote}: HomeTabListQuoteItemProps) {
    const currentUser = UserClient.getCurrentUser();
    const isAuthor: boolean = currentUser != null && currentUser.uid == quote.uid;

    return (
        <List.Item
            extra={PropertiesHelper.isStringNotEmpty(quote.imageSrc) ?
                <Image
                    width={272}
                    height={153}
                    alt='quateLogo'
                    src={quote.imageSrc}
                    preview={true}
                    style={{
                        objectFit: 'contain',  // 保持比例，不拉伸
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center'
                    }}
                />
                : null}
        >
            <List.Item.Meta
                title={
                    quote.source == null ? null :
                        <>
                            <Tooltip placement='top' title={'可能的出处'}>
                                <span style={{fontSize: '1.5rem', fontWeight: 'bold'}}>{quote.source}</span>
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
                            <span style={{
                                fontSize: '1rem',
                                color: '#0039f6',
                                fontWeight: 'bold'
                            }}>
                                    &emsp;传送门:&emsp;
                            </span>
                            <a href={quote.portal} target='_blank'>{quote.portal}</a>
                        </>
                }
            />
            <div className={'quote-md-preview'}>
                <MdPreview
                    id={`editor_id_for_browser_${quote.quoteId}`} value={quote.content}
                    sanitize={(html) => html}/>
            </div>
        </List.Item>
    );
}
