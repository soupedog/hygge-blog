import {lazy, StrictMode, Suspense} from 'react'
import {createRoot} from 'react-dom/client'
import {BrowserRouter, Route, Routes} from "react-router-dom";

import './App.css'

import Index from "./pages/Index.tsx";
import NotFound from "./pages/NotFound.tsx";
import Loading from "./pages/Loading.tsx";

// 懒加载模块，打包后可以看出来，这几个页面被单独打包了，页面可以在懒加载组件未完成时就展示(毕竟最先被展示的是 index 页)
const Login = lazy(() => import("./pages/Login.tsx"));

createRoot(document.getElementById('root')!).render(
    <StrictMode>
        {/*懒加载需要一同连用的组件，fallback 是加载未成功时要展示的内容*/}
        <Suspense fallback={<Loading key={"Loading"}/>}>
            <BrowserRouter>
                <Routes>
                    <Route path={"/signin"} element={<NotFound key={"NotFound"} delayTime={3000}/>}/>
                    <Route path={"/login"} element={<Login key={"Login"}/>}/>
                    <Route index path={"/"} element={<Index key={"Index"}/>}/>
                    {/*从上到下匹配，上方全未匹配命中则说明 404 */}
                    <Route path={"*"} element={<NotFound key={"NotFound"} delayTime={3000}/>}/>
                </Routes>
            </BrowserRouter>
        </Suspense>
    </StrictMode>,
)
