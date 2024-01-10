import React, {createContext, useEffect, useMemo, useState} from 'react';
import {ConfigProvider, message} from "antd";
import zhCN from "antd/locale/zh_CN";
import 'APlayer/dist/APlayer.min.css';
import "../../../style/browser.less"
import {useParams} from "react-router-dom";
import {ArticleDto, ArticleService} from "../../rest/ApiClient";
import {UrlHelper} from "../../utils/UtilContainer";
import BrowserView from "../component/browser/BrowserView";
import {AntdTreeNodeInfo} from "../component/markdown/util/MdHelper";

export interface BrowserState {
    currentArticle: ArticleDto | null;
    updateCurrentArticle: Function;
    tocEnable: boolean;
    updateTocEnable: Function;
    tocTree: AntdTreeNodeInfo[];
    updateTocTree: Function;
}

function Browser() {
    const [currentArticle, updateCurrentArticle] = useState(null);
    const [tocEnable, updateTocEnable] = useState(true);
    const [tocTree, updateTocTree] = useState([]);

    let {aid} = useParams();

    useEffect(() => {
        ArticleService.findArticleByAid(aid, (data) => {
            if (data?.main == null) {
                message.info("目标文章不存在，2 秒内自动跳转回主页，请稍后", 2000);
                UrlHelper.openNewPage({inNewTab: false, delayTime: 2000});
            } else {
                // @ts-ignore
                updateCurrentArticle(data.main);
            }
        })
        // 依赖静态值表示仅初始化时调用一次
    }, []);

    const state = useMemo(() => ({
        currentArticle: currentArticle,
        updateCurrentArticle: updateCurrentArticle,
        tocEnable: tocEnable,
        updateTocEnable: updateTocEnable,
        tocTree: tocTree,
        updateTocTree: updateTocTree
    }), [currentArticle, tocEnable, tocTree]);

    return (
        <ConfigProvider locale={zhCN}>
            <BrowserContext.Provider value={state}>
                <BrowserView/>
            </BrowserContext.Provider>
        </ConfigProvider>
    );
}

export const BrowserContext = createContext<BrowserState>({} as BrowserState);
export default Browser;