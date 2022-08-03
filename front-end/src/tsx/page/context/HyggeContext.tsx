import * as React from "react"

// 描述该组件 props 数据类型
export interface IndexContextValue {
}

export const IndexContext = React.createContext<IndexContextValue>(
    {}
);