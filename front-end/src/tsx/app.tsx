import React from 'react';
import {createRoot} from 'react-dom/client';
import {HashRouter, Route, Routes} from "react-router-dom";
import {IndexContainer} from "./page/IndexContainer";
import ArticleBrowserContainer from "./page/ArticleBrowserContainer";
import {SignInContainer} from "./page/SignInContainer";
import EditQuoteContainer from "./page/EditQuoteContainer";
import EditArticleContainer from "./page/EditArticleContainer";
import {SignInAutoContainer} from "./page/SignInAutoContainer";

const container: Element | null = document.getElementById('root');

if (container != null) {
    const root = createRoot(container);

    root.render(
        // <React.StrictMode>
        <HashRouter>
            <Routes>
                <Route path={""} element={<IndexContainer key={"index"}/>}/>
                <Route path={"/browser/:aid"} element={<ArticleBrowserContainer key={"browser"}/>}/>
                <Route path={"/signin"} element={<SignInContainer key={"signin"}/>}/>
                <Route path={"/signin/auto"} element={<SignInAutoContainer key={"signin-auto"}/>}/>
                <Route path={"/editor/article"} element={<EditArticleContainer key={"editor-article"}/>}/>
                <Route path={"/editor/article/:aid"} element={<EditArticleContainer key={"editor-article"}/>}/>
                <Route path={"/editor/quote/"} element={<EditQuoteContainer key={"editor-quote"}/>}/>
                <Route path={"/editor/quote/:quoteId"} element={<EditQuoteContainer key={"editor-quote"}/>}/>
            </Routes>
        </HashRouter>
        // </React.StrictMode>
    );
}