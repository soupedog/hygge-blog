import {useContext, useEffect, useState} from 'react';
import {Badge, List} from 'antd';
import {HomeContext} from '../context/HomeContext.tsx';
import {useHomeService} from '../../util/ApiService.ts';
import HomeTabQuoteListItem from './HomeTabQuoteListItem.tsx';

export default function HomeTabQuoteContent() {
    const {
        quoteInfo, setQuoteInfo
    } = useContext(HomeContext);

    const {fetchQuote} = useHomeService();

    const [currentPage, setCurrentPage] = useState(1);
    const [currentPageSize, setCurrentPageSize] = useState(5);

    useEffect(() => {
        fetchQuote.mutate({currentPage: currentPage, pageSize: currentPageSize},
            {
                onSuccess: (data) => {
                    setQuoteInfo({quoteList: data.quoteList, totalCount: data.totalCount});
                }
            }
        );
    }, [currentPage, currentPageSize]);

    return (
        <List
            itemLayout='vertical'
            size='large'
            pagination={{
                onChange: (page, pageSize) => {
                    if (page != currentPage) {
                        setCurrentPage(page);
                    }
                    if (pageSize != currentPageSize) {
                        setCurrentPageSize(pageSize);
                    }
                },
                showSizeChanger: true,
                total: quoteInfo.totalCount,
                pageSize: currentPageSize,

            }}
            dataSource={quoteInfo.quoteList}
            renderItem={(item) => {
                if (item.orderVal ?? 0 > 0) {
                    return (
                        <Badge.Ribbon key={`quoteItemBadge_${item.quoteId}`} text='顶置' color='red'>
                            <HomeTabQuoteListItem key={`quoteItem_${item.quoteId}`} quote={item}/>
                        </Badge.Ribbon>
                    );
                }

                return <HomeTabQuoteListItem key={`quoteItem_${item.quoteId}`} quote={item}/>
            }}
        />
    );
}
