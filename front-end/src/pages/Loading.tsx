import {useEffect} from 'react';
import {Spin} from "antd";

export interface LoadingProps {
    readonly text: string
}

export default function Loading({text}: LoadingProps) {
    useEffect(() => {
        console.log("Loading 被加载");
        // 依赖静态值表示仅初始化时调用一次
    }, []);

    return (
        <Spin description="Loading" size="large">
            {text}
        </Spin>
    );
}
