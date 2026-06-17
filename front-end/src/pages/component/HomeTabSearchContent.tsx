import {useContext, useEffect, useState} from 'react';
import type {ArticleDto, QuoteDto} from '../../util/ApiClient.ts';
import {HomeContext} from '../context/HomeContext.tsx';
import {Badge, List} from 'antd';
import HomeTabPostListItem from './HomeTabPostListItem.tsx';
import {useHomeService} from '../../util/ApiService.ts';
import HomeTabQuoteListItem from './HomeTabQuoteListItem.tsx';

export default function HomeTabSearchContent() {
    const {
        keyword,
        searchResultCurrentPage, setSearchResultCurrentPage,
        searchResultPageSize, setSearchResultPageSize,
        searchResult,
        searchResultOrderEnable,
        setSearchCategoryInfo,
        searchCategoryInfo,
        isPostType,
        fuzzySearch,
        searchPostSummaryByCid,
    } = useContext(HomeContext);

    const {searchQuoteByKeyword, searchPostSummaryByKeyword} = useHomeService();

    // 首次加载
    const [needRefreshData, setNeedRefreshData] = useState(false);

    // useEffect 至少会执行一次，用 needRefreshData 标识防止初始化时的重复查询
    useEffect(() => {
        if (needRefreshData) {
            if (searchCategoryInfo) {
                searchPostSummaryByCid({cid: searchCategoryInfo, currentPage: searchResultCurrentPage, pageSize: searchResultPageSize});
            } else {
                if (keyword) {
                    fuzzySearch({keyword: keyword, currentPage: searchResultCurrentPage, pageSize: searchResultPageSize});
                }
            }
        }

        setNeedRefreshData(true);
    }, [searchResultCurrentPage, searchResultPageSize]);


    return (
        <List
            itemLayout='vertical'
            size='large'
            bordered={false}
            loading={searchQuoteByKeyword.isPending || searchPostSummaryByKeyword.isPending}
            pagination={{
                onChange: (page, pageSize) => {
                    setSearchResultCurrentPage(page);
                    setSearchResultPageSize(pageSize);
                },
                showSizeChanger: true,
                total: searchResult.totalCount,
                current: searchResultCurrentPage,
                pageSize: searchResultPageSize,
            }}
            dataSource={searchResult.dataSet}
            renderItem={(item) => {
                if (isPostType(item)) {
                    const post = item as ArticleDto;

                    if (searchResultOrderEnable && post.orderCategory > 0) {
                        return (
                            <Badge.Ribbon key={`searchPostItemBadge_${post.aid}`} text='顶置' color='red'>
                                <HomeTabPostListItem key={`searchPostItem_${post.aid}`} post={item as ArticleDto}/>
                            </Badge.Ribbon>
                        );
                    }

                    if (post.articleState == 'PRIVATE') {
                        return (
                            <Badge.Ribbon key={`searchPostItemBadge_${post.aid}`} text='个人' color='blue'>
                                <HomeTabPostListItem key={`searchPostItem_${post.aid}`} post={item as ArticleDto}/>
                            </Badge.Ribbon>
                        );
                    }

                    return <HomeTabPostListItem key={`searchPostItem_${post.aid}`} post={post}/>
                } else {
                    const quote = item as QuoteDto;
                    return <HomeTabQuoteListItem key={`searchQuoteItem_${quote.quoteId}`} quote={quote}/>
                }
            }}
        />
    );
}
