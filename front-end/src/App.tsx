import {lazy, StrictMode, Suspense} from 'react'
import {createRoot} from 'react-dom/client'
import {BrowserRouter, Route, Routes} from "react-router-dom";

import './App.css'

import Home from "./pages/Home.tsx";
import Login from "./pages/Login.tsx";
import AppLayout from "./pages/component/AppLayout.tsx";
import NotFound from "./pages/NotFound.tsx";
import Loading from "./pages/Loading.tsx";

// 懒加载模块，打包后可以看出来，这几个页面被单独打包了，页面可以在懒加载组件未完成时就展示(毕竟最先被展示的是 index 页)
const Goods = lazy(() => import("./pages/Goods.tsx"));
const GoodsDetail = lazy(() => import("./pages/GoodsDetail.tsx"));
const MusicPlayer = lazy(() => import("./pages/MusicPlayer.tsx"));

createRoot(document.getElementById('root')!).render(
    <StrictMode>
        {/*懒加载需要一同连用的组件，fallback 是加载未成功时要展示的内容*/}
        <Suspense fallback={<Loading text={"test"}/>}>
            <BrowserRouter>
                <Routes>
                    <Route path={"/"} element={<AppLayout key={"layout"}/>}>
                        <Route index path={"/"} element={<Home key={"home"} text={""}/>}/>
                        <Route path={"/login"} element={<Login key={"login"}/>}/>
                        {/* 没 / 代表相对路径，相对于父节点*/}
                        <Route path={"goods"} element={<Goods key={"goods"}/>}/>
                        <Route path={"goods/detail/:gid"} element={<GoodsDetail key={"detail"}/>}/>
                        <Route path={"/musicPlayer"} element={<MusicPlayer key={"musicPlayer"}/>}/>
                        {/*从上到下匹配，上方全未匹配命中则说明该跳转到 musicPlayer 页面*/}
                        <Route path={"*"} element={<NotFound key={"notFound"} delayTime={3000}/>}/>
                    </Route>
                </Routes>
            </BrowserRouter>
        </Suspense>
    </StrictMode>,
)
