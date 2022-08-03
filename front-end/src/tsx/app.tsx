import React from 'react';
import {createRoot} from 'react-dom/client';
import {HashRouter, Route, Routes} from "react-router-dom";
import {IndexContainer} from "./page/IndexContainer";
import {ArticleBrowserContainer} from "./page/ArticleBrowserContainer";
import {SignInContainer} from "./page/SignInContainer";
import {EditArticleContainer} from "./page/EditArticleContainer";
import {EditQuoteContainer} from "./page/EditQuoteContainer";

const container: Element | null = document.getElementById('root');

if (container != null) {
    const root = createRoot(container);

    root.render(
        <React.StrictMode>
            <HashRouter>
                <Routes>
                    <Route path={""} element={<IndexContainer key={"index"}/>}/>
                    <Route path={"/browser"} element={<ArticleBrowserContainer key={"browser"}/>}/>
                    <Route path={"/signin"} element={<SignInContainer key={"signin"}/>}/>
                    <Route path={"/editor/article"} element={<EditArticleContainer key={"editor-article"}/>}/>
                    <Route path={"/editor/quote"} element={<EditQuoteContainer key={"editor-quote"}/>}/>
                </Routes>
            </HashRouter>
        </React.StrictMode>
    );
}