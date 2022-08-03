import * as React from "react"

// 描述该组件 props 数据类型
export interface ICCCCC {
    a?: number;
    b?: string;
}

let obj = {} as ICCCCC;

export const ThemeContext = React.createContext<ICCCCC>(
    obj
);