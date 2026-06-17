import {useEffect} from 'react';
import type {QuoteDto} from '../../util/ApiClient.ts';

export interface HomeTabListQuoteItemProps {
    readonly quote: QuoteDto;
}

export default function HomeTabQuoteListItem({quote}: HomeTabListQuoteItemProps) {

    useEffect(() => {
        // 依赖静态值表示仅初始化时调用一次
    }, []);

    return (
        <>
        </>
    );
}
