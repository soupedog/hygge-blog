import {useEffect} from 'react';
import {useNavigate} from "react-router-dom";
import UrlHelper from "../util/UrlHelper.ts";

export default function AppInit() {
    const navigate = useNavigate();

    useEffect(() => {
        // 依赖静态值表示仅初始化时调用一次
        UrlHelper.init(navigate);
    }, []);

    // 只负责初始化工具，无需渲染组件
    return null;
}
