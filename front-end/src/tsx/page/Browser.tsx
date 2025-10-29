import 'APlayer/dist/APlayer.min.css';
import "../../style/browser.less"

import React, {useEffect, useState} from 'react';
import {ConfigProvider, message} from "antd";
import zhCN from "antd/locale/zh_CN";
import {useParams} from "react-router-dom";
import {ArticleService} from "../rest/ApiClient";
import BrowserView from "../component/browser/BrowserView";
import {UrlHelper} from "../util/UtilContainer";

function Browser() {
    const [currentArticle, updateCurrentArticle] = useState(null);
    const {aid} = useParams();

    useEffect(() => {
        ArticleService.findArticleByAid(aid, (data) => {
            if (data?.main == null) {
                message.info("目标文章不存在，2 秒内自动跳转回主页，请稍后", 2000);
                UrlHelper.openNewPage({inNewTab: false, delayTime: 2000});
            } else {
                // @ts-ignore
                updateCurrentArticle(data.main);

                // 改页面标题
                document.title = data.main.title;
            }
        })
        // 依赖静态值表示仅初始化时调用一次
    }, []);

    return (
        <ConfigProvider locale={zhCN}>
            <BrowserView article={currentArticle}/>
        </ConfigProvider>
    );
}

export default Browser;