import {createContext, type ReactNode, useState} from 'react';
import {HomeKeywordType} from '../../enums/EnumKeeper.ts';
import {useSearchParams} from 'react-router-dom';
import {useHomeService} from '../../util/ApiService.ts';
import {message} from 'antd';
import {appConfiguration} from '../../configuration/app.configuration.ts';
import type {ArticleDto, CategoryDto, PostInCategorySearchInput, QuoteDto} from '../../util/ApiClient.ts';

const toastZIndex = appConfiguration.toastDefaultZIndex;

export interface HomeState {
    searchParams: URLSearchParams;
    setSearchParams: Function;
    collapsed: boolean;
    setCollapsed: Function;
    keyword?: string;
    setKeyword: Function;
    keywordType: HomeKeywordType;
    setKeywordType: Function;
    searchTabCurrentPage: number;
    setSearchTabCurrentPage: Function;
    searchTabPageSize: number;
    setSearchTabPageSize: Function;
    searchResult: Array<ArticleDto | QuoteDto>;
    setSearchResult: Function;
    searchResultTotalCount: number;
    setSearchResultTotalCount: Function;
    categoryCollapsed: boolean;
    setCategoryCollapsed: Function;
    categorySizeInRow: number;
    setCategorySizeInRow: Function;
    categoryList: Array<CategoryDto>;
    setCategoryList: Function;
    fuzzySearch: Function;
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
    const [searchTabCurrentPage, setSearchTabCurrentPage] = useState(1);
    const [searchTabPageSize, setSearchTabPageSize] = useState(5);
    const [searchResult, setSearchResult] = useState<Array<ArticleDto | QuoteDto>>([]);
    const [searchResultTotalCount, setSearchResultTotalCount] = useState(0);

    // 博客类别面板 是否收起
    const [categoryCollapsed, setCategoryCollapsed] = useState(false);
    // 博客类别一行显示几个
    const [categorySizeInRow, setCategorySizeInRow] = useState(5);
    const [categoryList, setCategoryList] = useState<Array<CategoryDto>>([]);

    const fuzzySearch = () => {
        if (keyword != null) {
            if (keywordType == HomeKeywordType.POST) {
                searchPostSummaryByKeyword.mutate(
                    {
                        keyword: keyword,
                        currentPage: searchTabCurrentPage,
                        pageSize: searchTabPageSize,
                    },
                    {
                        onSuccess: (data) => {
                            setSearchResult(data.articleSummaryList);
                            setSearchResultTotalCount(data.totalCount);
                            //TODO 设置搜索 Tab 为激活状态
                        }
                    }
                );
            } else {
                searchQuoteByKeyword.mutate(
                    {
                        keyword: keyword,
                        currentPage: searchTabCurrentPage,
                        pageSize: searchTabPageSize,
                    },
                    {
                        onSuccess: (data) => {
                            setSearchResult(data.quoteList);
                            setSearchResultTotalCount(data.totalCount);
                            //TODO 设置搜索 Tab 为激活状态
                        }
                    }
                );
            }
        } else {
            message.warning({content: '搜索关键字不可为空！', style: {zIndex: toastZIndex}});
        }
    };

    const searchPostSummaryByCid = (input: PostInCategorySearchInput) => {
        fetchPostSummaryByCid.mutate(input, {
            onSuccess: (data) => {
                setSearchResult(data.articleSummaryList);
                setSearchResultTotalCount(data.totalCount);
            }
        });
    };

    return (
        <HomeContext value={{
            searchParams, setSearchParams,
            collapsed, setCollapsed,
            keyword, setKeyword,
            keywordType, setKeywordType,
            categoryCollapsed, setCategoryCollapsed,
            categorySizeInRow, setCategorySizeInRow,
            categoryList, setCategoryList,
            searchResult, setSearchResult,
            searchTabCurrentPage, setSearchTabCurrentPage,
            searchTabPageSize, setSearchTabPageSize,
            searchResultTotalCount, setSearchResultTotalCount,
            fuzzySearch,
            searchPostSummaryByCid
        }}>
            {children}
        </HomeContext>
    );
}
