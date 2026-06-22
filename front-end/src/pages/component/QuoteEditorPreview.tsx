import {Badge, BorderBeam, Card, List, Typography} from 'antd';
import HomeTabQuoteListItem from './HomeTabQuoteListItem.tsx';
import Title from 'antd/es/typography/Title';
import {useContext} from 'react';
import {QuoteEditorContext} from '../context/QuoteEditorContext.tsx';

export default function QuoteEditorPreview() {
    const {
        quote,
    } = useContext(QuoteEditorContext);

    const dataSource = quote ? [quote] : [];

    return (
        <BorderBeam>
            <Card style={{margin: '2rem'}}>
                <Typography>
                    <Title level={3} style={{color: '#0c13d1'}}>预览效果：</Title>
                </Typography>
                <List
                    itemLayout='vertical'
                    size='large'
                    dataSource={dataSource}
                    renderItem={(item) => {
                        if (item.orderVal ?? 0 > 0) {
                            return (
                                <Badge.Ribbon key={`quoteItemBadge_${item.quoteId}`} text='顶置' color='red'>
                                    <HomeTabQuoteListItem key={`quoteItem_${item.quoteId}`} quote={item} noEditor/>
                                </Badge.Ribbon>
                            );
                        }

                        return <HomeTabQuoteListItem key={`quoteItem_${item.quoteId}`} quote={item} noEditor/>
                    }}
                />
            </Card>
        </BorderBeam>
    );
}
