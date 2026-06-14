import {lazy, StrictMode, Suspense} from 'react'
import {createRoot} from 'react-dom/client'

import './index.css'

import Loading from './pages/Loading.tsx';
import {BrowserRouter} from 'react-router-dom';

const AppInit = lazy(() => import('./pages/./App'));

createRoot(document.getElementById('root')!).render(
    <StrictMode>
        {/*懒加载需要一同连用的组件，fallback 是加载未成功时要展示的内容*/}
        <Suspense fallback={<Loading key={'Loading'}/>}>
            <BrowserRouter>
                <AppInit/>
            </BrowserRouter>
        </Suspense>
    </StrictMode>
);
