import {useContext, useEffect, useState} from 'react';
import type {ArticleDto, FePageQueryResponse, QuoteDto} from '../../util/ApiClient.ts';
import {Badge, List} from 'antd';
import {HomeContext} from '../context/HomeContext.tsx';
import HomeTabPostListItem from './HomeTabPostListItem.tsx';
import {useHomeService} from '../../util/ApiService.ts';

export interface HomeTabContentProps {
    readonly initData?: FePageQueryResponse;
    readonly tid: string;
}

export default function HomeTabPostContent({initData, tid}: HomeTabContentProps) {
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
            bordered={false}
            loading={fetchPostSummaryByTid.isPending}
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
                total: totalCount,
                current: currentPage,
                pageSize: currentPageSize,
            }}
            dataSource={listData}
            renderItem={(item) => {
                const post = item as ArticleDto;

                if (post.orderCategory > 0) {
                    return (
                        <Badge.Ribbon key={`postItemBadge_${post.aid}`} text='顶置' color='red'>
                            <HomeTabPostListItem key={`postItem_${post.aid}`} post={item as ArticleDto}/>
                        </Badge.Ribbon>
                    );
                }

                if (post.articleState == 'PRIVATE') {
                    return (
                        <Badge.Ribbon key={`postItemBadge_${post.aid}`} text='个人' color='blue'>
                            <HomeTabPostListItem key={`postItem_${post.aid}`} post={item as ArticleDto}/>
                        </Badge.Ribbon>
                    );
                }

                return <HomeTabPostListItem key={`postItem_${post.aid}`} post={post}/>
            }}
        />
    );
}
