import {useContext, useEffect, useState} from 'react';
import type {ArticleDto, FePageQueryResponse, QuoteDto} from '../../util/ApiClient.ts';
import {List} from 'antd';
import {HomeContext} from '../context/HomeContext.tsx';
import HomeTabListPostItem from './HomeTabListPostItem.tsx';
import HomeTabListQuoteItem from './HomeTabListQuoteItem.tsx';
import {useHomeService} from '../../util/ApiService.ts';

export interface HomeTabContentProps {
    readonly initData?: FePageQueryResponse;
    readonly tid: string;
}

export default function HomeTabContent({initData, tid}: HomeTabContentProps) {
    const {
        isPostType
    } = useContext(HomeContext);

    const {fetchPostSummaryByTid} = useHomeService();

    const [currentPage, setCurrentPage] = useState(1);
    const [currentPageSize, setCurrentPageSize] = useState(5);
    const [totalCount, setTotalCount] = useState(initData?.totalCount ?? 0);
    const [listData, setListData] = useState<Array<QuoteDto | ArticleDto>>(initData?.dataSet ?? []);
    const [needRefreshData, setNeedRefreshData] = useState(listData.length == 0);

    useEffect(() => {
        if (needRefreshData) {
            fetchPostSummaryByTid.mutate({tid: tid, currentPage: currentPage, pageSize: currentPageSize},
                {
                    onSuccess: (data) => {
                        setTotalCount(data.totalCount);
                        setListData(data.articleSummaryList);
                    }
                }
            );
        }
        setNeedRefreshData(true);
    }, [currentPage, currentPageSize]);

    return (
        <List
            itemLayout='vertical'
            size='large'
            loading={fetchPostSummaryByTid.isPending}
            pagination={{
                onChange: (page, pageSize) => {
                    console.log(`page:${page} pageSize:${pageSize}`);
                    if (page != currentPage) {
                        setCurrentPage(page);
                    } else if (pageSize != currentPageSize) {
                        setCurrentPageSize(pageSize);
                    }
                },
                showSizeChanger: true,
                total: totalCount,
                current: currentPage,
                pageSize: currentPageSize,
            }}
            dataSource={listData}
            renderItem={(item) => (
                isPostType(item) ? <HomeTabListPostItem post={item as ArticleDto}/> : <HomeTabListQuoteItem quote={item as QuoteDto}/>
            )}
        />
    );
}
