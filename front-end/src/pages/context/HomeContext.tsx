import {createContext, type ReactNode, useState} from 'react';
import {HomeKeywordType} from '../../enums/EnumKeeper.ts';
import {useSearchParams} from 'react-router-dom';
import {useHomeService} from '../../util/ApiService.ts';
import type {AnnouncementDto, ArticleDto, CategoryDto, FePageQueryResponse, KeywordSearchInput, PostInCategorySearchInput, QuoteResponse, TopicOverviewInfo} from '../../util/ApiClient.ts';

export interface HomeState {
    searchParams: URLSearchParams;
    setSearchParams: Function;
    collapsed: boolean;
    setCollapsed: Function;
    firstTopicInitResult: FePageQueryResponse;
    setFirstTopicInitResult: (input: FePageQueryResponse) => void;
    keyword?: string;
    setKeyword: Function;
    keywordType: HomeKeywordType;
    setKeywordType: Function;
    searchResult: FePageQueryResponse;
    setSearchResult: (input: FePageQueryResponse) => void;
    searchResultCurrentPage: number;
    setSearchResultCurrentPage: Function;
    searchResultPageSize: number;
    setSearchResultPageSize: Function;
    searchResultOrderEnable: boolean;
    setSearchResultOrderEnable: Function;
    // cid
    searchCategoryInfo?: string;
    setSearchCategoryInfo: Function;
    categoryCollapsed: boolean;
    setCategoryCollapsed: Function;
    // tid-Array<CategoryDto>
    categoryInfoMap: Map<string, Array<CategoryDto>>;
    currentCategoryInfo: Array<CategoryDto>;
    setCurrentCategoryInfo: Function;
    addCategoryInfoOfTopic: (input: { tid?: string, list?: Array<CategoryDto> }) => void;
    activeTap: string;
    setActiveTap: Function;
    topicOverviewInfoList: Array<TopicOverviewInfo>;
    setTopicOverviewInfoList: Function;
    quoteInfo: QuoteResponse;
    setQuoteInfo: (input: QuoteResponse) => void;
    announcementInfoList: Array<AnnouncementDto>;
    setAnnouncementInfoList: Function;
    isPostType: (input: unknown) => boolean;
    fuzzySearch: (input: KeywordSearchInput) => void;
    searchPostSummaryByCid: (input: PostInCategorySearchInput) => void;
}

export const HomeContext = createContext<HomeState>({} as HomeState);

export const HomeProvider = ({children}: { children: ReactNode }) => {
    const {searchPostSummaryByKeyword, searchQuoteByKeyword, fetchPostSummaryByCid} = useHomeService();

    // HomeSider 是否收起
    const [collapsed, setCollapsed] = useState(true);
    const [searchParams, setSearchParams] = useSearchParams();
    // HomeHeader 搜索关键字
    const [keyword, setKeyword] = useState(searchParams.get('keyword') || undefined);
    const [keywordType, setKeywordType] = useState<HomeKeywordType>(HomeKeywordType.POST);
    // 主页初始化加载第一次网络请求中包含了第一个主题的内容，第一个主题拉取第一页内容无需发起请求
    const [firstTopicInitResult, setFirstTopicInitResult] = useState<FePageQueryResponse>({dataSet: [], totalCount: 0});
    const [searchResult, setSearchResult] = useState<FePageQueryResponse>({dataSet: [], totalCount: 0});
    const [searchResultCurrentPage, setSearchResultCurrentPage] = useState(1);
    const [searchResultPageSize, setSearchResultPageSize] = useState(5);
    const [searchResultOrderEnable, setSearchResultOrderEnable] = useState(false);
    const [searchCategoryInfo, setSearchCategoryInfo] = useState<string | undefined>(undefined);

    // 博客类别面板 是否收起
    const [categoryCollapsed, setCategoryCollapsed] = useState(false);
    const [categoryInfoMap, setCategoryInfoMap] = useState(new Map());
    const [currentCategoryInfo, setCurrentCategoryInfo] = useState([]);
    const [activeTap, setActiveTap] = useState('');
    const [topicOverviewInfoList, setTopicOverviewInfoList] = useState<Array<TopicOverviewInfo>>([]);
    const [quoteInfo, setQuoteInfo] = useState<QuoteResponse>({quoteList: [], totalCount: 0});
    const [announcementInfoList, setAnnouncementInfoList] = useState<Array<AnnouncementDto>>([]);

    const isPostType = (input: unknown) => {
        // 如果存在 aid 属性且不为 undefined，返回 true
        return (input as ArticleDto).aid !== undefined;
    };

    const addCategoryInfoOfTopic = (input: { tid?: string, list?: Array<CategoryDto> }) => {
        if (input.tid && input.list && input.list.length > 0) {
            categoryInfoMap.set(input.tid, input.list);
        }
    };

    const fuzzySearch = (input: KeywordSearchInput) => {
        if (keywordType == HomeKeywordType.POST) {
            searchPostSummaryByKeyword.mutate(
                {
                    keyword: input.keyword,
                    currentPage: input.currentPage,
                    pageSize: input.pageSize,
                },
                {
                    onSuccess: (data) => {
                        setSearchResult({dataSet: data.articleSummaryList, totalCount: data.totalCount});
                        setActiveTap('搜索结果');
                    }
                }
            );
        } else {
            searchQuoteByKeyword.mutate(
                {
                    keyword: input.keyword,
                    currentPage: input.currentPage,
                    pageSize: input.pageSize,
                },
                {
                    onSuccess: (data) => {
                        setSearchResult({dataSet: data.quoteList, totalCount: data.totalCount});
                        setActiveTap('搜索结果');
                    }
                }
            );
        }
    };

    const searchPostSummaryByCid = (input: PostInCategorySearchInput) => {
        fetchPostSummaryByCid.mutate(input, {
            onSuccess: (data) => {
                setSearchResult({dataSet: data.articleSummaryList, totalCount: data.totalCount});
            }
        });
    };

    return (
        <HomeContext value={{
            searchParams, setSearchParams,
            collapsed, setCollapsed,
            firstTopicInitResult, setFirstTopicInitResult,
            searchResult, setSearchResult,
            searchResultCurrentPage: searchResultCurrentPage, setSearchResultCurrentPage: setSearchResultCurrentPage,
            searchResultPageSize: searchResultPageSize, setSearchResultPageSize: setSearchResultPageSize,
            searchResultOrderEnable, setSearchResultOrderEnable,
            searchCategoryInfo, setSearchCategoryInfo,
            keyword, setKeyword,
            keywordType, setKeywordType,
            categoryCollapsed, setCategoryCollapsed,
            categoryInfoMap, addCategoryInfoOfTopic,
            currentCategoryInfo, setCurrentCategoryInfo,
            activeTap, setActiveTap,
            topicOverviewInfoList, setTopicOverviewInfoList,
            quoteInfo, setQuoteInfo,
            announcementInfoList, setAnnouncementInfoList,
            isPostType,
            fuzzySearch,
            searchPostSummaryByCid
        }}>
            {children}
        </HomeContext>
    );
}
