import React from 'react';
import {Button, Result} from "antd";
import {UrlHelper} from "../../utils/UtilContainer";

function NotFound() {
    // 3 秒内自动跳转到主页
    UrlHelper.openNewPage({inNewTab: false, delayTime: 3000});

    return (
        <Result
            status="404"
            title="目标资源未找到"
            subTitle="很抱歉, 您所访问的资源不存在，将在 3 秒内自动为您返回主页."
            extra={
                <Button type="primary" onClick={() => {
                    UrlHelper.openNewPage({inNewTab: false});
                }}>立即返回主页</Button>
            }
        />
    );
}

export default NotFound;