import {lazy, useEffect} from 'react';
import {Route, Routes, useNavigate} from 'react-router-dom';
import {QueryClient, QueryClientProvider} from '@tanstack/react-query';

import 'md-editor-rt/lib/style.css';
import "@vavt/rt-extension/lib/asset/ExportPDF.css";
import "@vavt/rt-extension/lib/asset/Mark.css";
import 'highlight.js/styles/atom-one-dark.css';

import UrlHelper from '../util/UrlHelper.ts';
import {UserClient} from '../util/ApiClient.ts';
import {ClientScope} from '../enums/EnumKeeper.ts';
import {httpClient} from '../util/HttpClient.ts';
import {config} from 'md-editor-rt';
// @ts-ignore
import MarkExtension from 'markdown-it-mark';
import * as prettier from 'prettier';
import parserMarkdown from 'prettier/plugins/markdown';
import {keymap} from '@codemirror/view';
import highlight from 'highlight.js';
import mermaid from 'mermaid';
import {message} from 'antd';
import {appConfiguration} from '../configuration/app.configuration.ts';
// 创建 QueryClient 实例
const queryClient = new QueryClient();
// 懒加载模块，打包后可以看出来，这几个页面被单独打包了，页面可以在懒加载组件未完成时就展示

const Home = lazy(() => import('./Home.tsx'));
const NotFound = lazy(() => import('./NotFound.tsx'));
const Signin = lazy(() => import('./Signin.tsx'));
const PostBrowser = lazy(() => import('./PostBrowser.tsx'));

// 在应用初始化时配置
message.config({
    getContainer: () => document.getElementById('root') || document.body,
    top: 64,  // 可调整距离顶部的距离
});

const toastZIndex = appConfiguration.toastDefaultZIndex;
// Markdown 工具全局配置
config({
    markdownItConfig(md) {
        md.use(MarkExtension);
    },
    editorExtensions: {
        prettier: {
            prettierInstance: prettier,
            parserMarkdownInstance: parserMarkdown
        },
        highlight: {
            instance: highlight
        },
        mermaid: {
            instance: mermaid
        }
    },
    codeMirrorExtensions(extensions, {keyBindings}) {
        // 1. 先把旧的快捷键映射移除
        const newExtensions = [...extensions].filter((item) => {
            return item.type !== 'keymap';
        });

        // 2. 参考快捷键配置的源码，找到 CtrlF 的配置项在 keyBindings 中的位置
        const CtrlF = keyBindings.find((i) => i.key === 'Ctrl-f');

        const prettierAction = CtrlF!.shift

        // 3. 把美化操作绑定到 ctrl + alt + L 上
        // https://codemirror.net/docs/ref/#commands
        const MyCtrlL = {
            key: 'Ctrl-l',
            mac: 'Cmd-l',
            // @ts-ignore
            any(_view, e) {
                if ((e.ctrlKey || e.metaKey) && e.altKey && e.code === 'KeyL') {
                    // @ts-ignore
                    prettierAction();
                    message.success({content: '已将文本进行美化排版。', style: {zIndex: toastZIndex}})
                }
            }
        };

        // 4. 把修改后的快捷键放到待构建扩展的数组中
        const newKeyBindings = [MyCtrlL, ...keyBindings.filter((i) => i.key !== 'Ctrl-l')];

        newExtensions.push({
            type: 'newKeymap',
            // @ts-ignore
            extension: keymap.of(newKeyBindings)
        });

        return newExtensions;
    }
});


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
