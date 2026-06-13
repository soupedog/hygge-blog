import {useEffect} from 'react';
import {Button, Result} from "antd";
import {useNavigate} from "react-router-dom";

export interface NotFoundProps {
    readonly delayTime: number
}

export default function NotFound({delayTime}: NotFoundProps) {
    const navigate = useNavigate();

    useEffect(() => {
        // 依赖静态值表示仅初始化时调用一次
        window.setTimeout(function () {
            // replace 为 true 代表跳转后无法通过后退返回页面
            navigate("/", {replace: true});
        }, delayTime);
    }, []);

    return (
        <Result
            status="404"
            title="目标资源未找到"
            subTitle="很抱歉, 您所访问的资源不存在，将在 3 秒内自动为您返回主页。"
            extra={
                <Button type="primary" onClick={() => {
                    // replace 为 true 代表跳转后无法通过后退返回页面
                    navigate("/", {replace: true});
                }}>
                    立即返回主页
                </Button>
            }
        />
    );
}
