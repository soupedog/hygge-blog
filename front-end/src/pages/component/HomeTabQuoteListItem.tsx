import * as React from 'react';
import {type QuoteDto, UserClient} from '../../util/ApiClient.ts';
import {Card, Image, List, Row, Space, Splitter, Tooltip} from 'antd';
import PropertiesHelper from '../../util/PropertiesHelper.ts';
import {FormOutlined} from '@ant-design/icons';
import UrlHelper from '../../util/UrlHelper.ts';
import {MdPreview} from 'md-editor-rt';
import clsx from 'clsx';

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

    const hasRemarks = PropertiesHelper.isStringNotEmpty(quote.remarks);

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
            <Row>
                {hasRemarks ?
                    <Splitter style={{boxShadow: '0 0 10px rgba(0, 0, 0, 0.1)'}}>
                        <Splitter.Panel>
                            <Card>
                                <div className={'quote-md-preview'}>
                                    <MdPreview
                                        id={`md-id-quote-${quote.quoteId}`} value={quote.content}
                                        sanitize={(html) => html}/>
                                </div>
                            </Card>
                        </Splitter.Panel>
                        <Splitter.Panel defaultSize='30%' min='10%' max='90%'>
                            <Card className={'quote-md-remark-preview'}>
                                <div className={clsx([
                                    'inlineBlock',
                                    'autoOmit',
                                    'textAlignRight',
                                    'fullWidth',
                                    'quote-remarks-title'
                                ])}>
                                    —— 备注&nbsp;&nbsp;
                                </div>
                                <MdPreview
                                    id={`md-id-quote-remarks-${quote.quoteId}`} value={`${quote.remarks}`}
                                    sanitize={(html) => html}/>
                            </Card>
                        </Splitter.Panel>
                    </Splitter>
                    :
                    <Card>
                        <div className={'quote-md-preview'}>
                            <MdPreview
                                id={`md-id-quote-${quote.quoteId}`} value={quote.content}
                                sanitize={(html) => html}/>
                        </div>
                    </Card>
                }
            </Row>
        </List.Item>
    );
}
