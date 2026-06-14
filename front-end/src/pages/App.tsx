import {lazy, useEffect} from 'react';
import {Route, Routes, useNavigate} from 'react-router-dom';
import {QueryClient, QueryClientProvider} from '@tanstack/react-query';

import UrlHelper from '../util/UrlHelper.ts';
import {UserClient} from '../util/ApiClient.ts';
import {ClientScope} from '../enums/EnumKeeper.ts';
import {httpClient} from '../util/HttpClient.ts';

// 创建 QueryClient 实例
const queryClient = new QueryClient();
// 懒加载模块，打包后可以看出来，这几个页面被单独打包了，页面可以在懒加载组件未完成时就展示

const Home = lazy(() => import('./Home.tsx'));
const NotFound = lazy(() => import('./NotFound.tsx'));
const Signin = lazy(() => import('./Signin.tsx'));
const PostBrowser = lazy(() => import('./PostBrowser.tsx'));

export default function App() {
    const navigate = useNavigate();

    useEffect(() => {
        // 依赖静态值表示仅初始化时调用一次
        UrlHelper.init(navigate);
        UserClient.init(ClientScope.WEB);

        // 初始化单例
        httpClient.initInterceptors();
    }, []);

    return (
        <QueryClientProvider client={queryClient}>
            <Routes>
                <Route path={'/post/:pid'} element={<PostBrowser key={'PostBrowser'}/>}/>
                <Route path={'/manage/editor/post'} element={<NotFound key={'NotFound'} delayTime={3000}/>}/>
                <Route path={'/manage/editor/quote'} element={<NotFound key={'NotFound'} delayTime={3000}/>}/>
                <Route path={'/manage/file/glance'} element={<NotFound key={'NotFound'} delayTime={3000}/>}/>
                <Route path={'/manage/file/operate'} element={<NotFound key={'NotFound'} delayTime={3000}/>}/>
                <Route path={'/signup'} element={<NotFound key={'NotFound'} delayTime={3000}/>}/>
                <Route path={'/signin'} element={<Signin key={'Signin'}/>}/>
                <Route path={'/'} element={<Home key={'Home'}/>}/>
                {/*从上到下匹配，上方全未匹配命中则说明 404 */}
                <Route path={'*'} element={<NotFound key={'NotFound'} delayTime={3000}/>}/>
            </Routes>
        </QueryClientProvider>
    );
}
