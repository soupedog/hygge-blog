import React from 'react';
import {createRoot} from 'react-dom/client';
import {IndexContainer} from "./page/IndexContainer"

const container: Element | null = document.getElementById('root');

if (container != null) {
    const root = createRoot(container);
    root.render(
        <React.StrictMode>
            <IndexContainer/>
        </React.StrictMode>
    );
}