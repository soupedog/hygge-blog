import {useEffect} from 'react';

export default function Index() {
    useEffect(() => {
        // 依赖静态值表示仅初始化时调用一次
        document.title = "主页";
    }, []);

    return (
        <div>
            主页
        </div>
    );
}
