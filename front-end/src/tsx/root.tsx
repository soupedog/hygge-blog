import React from 'react';
import {createRoot} from 'react-dom/client';
import {HashRouter as RouteRoot} from "react-router-dom";
import {App} from "./page/App"

const container: Element | null = document.getElementById('root');

if (container != null) {
    const root = createRoot(container);
    root.render(
        <React.StrictMode>
            <RouteRoot>
                <App/>
            </RouteRoot>
        </React.StrictMode>
    );
}