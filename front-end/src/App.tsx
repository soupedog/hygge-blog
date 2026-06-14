import {lazy, StrictMode, Suspense} from 'react'
import {createRoot} from 'react-dom/client'
import {BrowserRouter, Route, Routes} from 'react-router-dom';

import './App.css'

import Loading from './pages/Loading.tsx';
import {QueryClient, QueryClientProvider} from '@tanstack/react-query';

// 懒加载模块，打包后可以看出来，这几个页面被单独打包了，页面可以在懒加载组件未完成时就展示
const Index = lazy(() => import('./pages/./Index.tsx'));
const NotFound = lazy(() => import('./pages/./NotFound.tsx'));
const Signin = lazy(() => import('./pages/./Signin.tsx'));
const AppInit = lazy(() => import('./pages/AppInit.tsx'));

// 创建 QueryClient 实例
const queryClient = new QueryClient();

createRoot(document.getElementById('root')!).render(
    <StrictMode>
        {/*懒加载需要一同连用的组件，fallback 是加载未成功时要展示的内容*/}
        <Suspense fallback={<Loading key={'Loading'}/>}>
            {/*客户端查询缓存组件 react-query*/}
            <QueryClientProvider client={queryClient}>
                <BrowserRouter>
                    <AppInit/>
                    <Routes>
                        <Route path={'/read/:aid'} element={<NotFound key={'NotFound'} delayTime={3000}/>}/>
                        <Route path={'/manage/editor/post'} element={<NotFound key={'NotFound'} delayTime={3000}/>}/>
                        <Route path={'/manage/editor/quote'} element={<NotFound key={'NotFound'} delayTime={3000}/>}/>
                        <Route path={'/manage/file/glance'} element={<NotFound key={'NotFound'} delayTime={3000}/>}/>
                        <Route path={'/manage/file/operate'} element={<NotFound key={'NotFound'} delayTime={3000}/>}/>
                        <Route path={'/signup'} element={<NotFound key={'NotFound'} delayTime={3000}/>}/>
                        <Route path={'/signin'} element={<Signin key={'Signin'}/>}/>
                        <Route path={'/'} element={<Index key={'Index'}/>}/>
                        {/*从上到下匹配，上方全未匹配命中则说明 404 */}
                        <Route path={'*'} element={<NotFound key={'NotFound'} delayTime={3000}/>}/>
                    </Routes>
                </BrowserRouter>
            </QueryClientProvider>
        </Suspense>
    </StrictMode>,
);
