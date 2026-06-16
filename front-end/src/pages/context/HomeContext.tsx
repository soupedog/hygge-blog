import {createContext, type ReactNode, useState} from 'react';
import {HomeKeywordType} from '../../enums/EnumKeeper.ts';
import {useSearchParams} from 'react-router-dom';

export interface HomeState {
    searchParams: URLSearchParams;
    setSearchParams: Function;
    collapsed: boolean;
    setCollapsed: Function;
    keyword?: string;
    setKeyword: Function;
    keywordType: HomeKeywordType;
    setKeywordType: Function;
    fuzzySearch: Function;
}

export const HomeContext = createContext<HomeState>({} as HomeState);

export const HomeProvider = ({children}: { children: ReactNode }) => {
    // HomeSider 是否收起
    const [collapsed, setCollapsed] = useState(false);
    const [searchParams, setSearchParams] = useSearchParams();
    // HomeHeader 搜索关键字
    const [keyword, setKeyword] = useState(searchParams.get('keyword') || undefined);
    const [keywordType, setKeywordType] = useState<HomeKeywordType>(HomeKeywordType.POST);

    const fuzzySearch = () => {
        console.log(`查询类型：${keywordType} 关键字：${keyword}`);
    };

    return (
        <HomeContext value={{
            searchParams, setSearchParams,
            collapsed, setCollapsed,
            keyword, setKeyword,
            keywordType, setKeywordType,
            fuzzySearch
        }}>
            {children}
        </HomeContext>
    );
}
