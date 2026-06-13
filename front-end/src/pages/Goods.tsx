import Search from "antd/es/input/Search";
import {useEffect, useState} from 'react';
import {useSearchParams} from "react-router-dom";

export default function Goods() {
    const [searchParams, setSearchParams] = useSearchParams();
    const [keyword, setKeyword] = useState(searchParams.get("keyword") || "");

    useEffect(() => {
        // 必须新创建对象，沿用 searchParams 的引用会判定为未变化，不会进行数据更新
        // 导致一个问题：有多少个 QueryString 都要挨个检查
        let params = {};
        if (keyword) {
            // @ts-ignore
            params.keyword = keyword;
            setSearchParams(params);
        }
    }, [keyword]);
    // 仅当 keyword 发生变化时触发

    return (
        <div style={{width: "600px", margin: "0 auto"}}>
            <Search
                placeholder="输入搜索关键字"
                enterButton="搜索"
                size="large"
                value={keyword}
                onChange={(event) => {
                    setKeyword(event.target.value);
                }}
                onSearch={() => {
                    alert("进行搜索操作");
                }}
            />
        </div>
    );
}
