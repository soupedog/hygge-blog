import {useEffect} from 'react';
import {Button, Result} from 'antd';

import UrlHelper from '../util/UrlHelper.ts';

export interface NotFoundProps {
    readonly delayTime: number;
}

export default function NotFound({delayTime}: NotFoundProps) {
    useEffect(() => {
        // 依赖静态值表示仅初始化时调用一次
        window.setTimeout(() => {
            UrlHelper.navigateTo({path: '/', canBack: false, delayTime: delayTime});
        }, delayTime);
    }, []);

    return (
        <Result
            status='404'
            title='目标资源未找到'
            subTitle='很抱歉, 您所访问的资源不存在，将在 3 秒内自动为您返回主页。'
            extra={
                <Button type='primary' onClick={() => {
                    UrlHelper.navigateTo({path: '/', canBack: false, delayTime: delayTime});
                }}>
                    立即返回主页
                </Button>
            }
        />
    );
}
