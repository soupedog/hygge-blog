import {lazy, useEffect, useLayoutEffect, useState} from 'react';
import {Route, Routes, useLocation, useNavigate} from 'react-router-dom';
import {QueryClient, QueryClientProvider} from '@tanstack/react-query';

import 'md-editor-rt/lib/style.css';
import '@vavt/rt-extension/lib/asset/ExportPDF.css';
import '@vavt/rt-extension/lib/asset/Mark.css';
import 'highlight.js/styles/atom-one-dark.css';

import UrlHelper from '../util/UrlHelper.ts';
import {UserClient} from '../util/ApiClient.ts';
import {ClientScope, StorageKey} from '../enums/EnumKeeper.ts';
import {config} from 'md-editor-rt';
// @ts-ignore
import MarkExtension from 'markdown-it-mark';
import * as prettier from 'prettier';
import parserMarkdown from 'prettier/plugins/markdown';
import {keymap} from '@codemirror/view';
import highlight from 'highlight.js';
import mermaid from 'mermaid';
import {ConfigProvider, message, Modal} from 'antd';
import {HomeProvider} from './context/HomeContext.tsx';
import zhCN from 'antd/lib/locale/zh_CN';
import {QuoteEditorContextProvider} from './context/QuoteEditorContext.tsx';
import {PostEditorContextProvider} from './context/PostEditorContext.tsx';
import {detectDevice} from '@al01/detectdevice/dist';
import StorageHelper from '../util/StorageHelper.ts';
// 创建 QueryClient 实例
const queryClient = new QueryClient();
// 懒加载模块，打包后可以看出来，这几个页面被单独打包了，页面可以在懒加载组件未完成时就展示

const Home = lazy(() => import('./Home.tsx'));
const NotFound = lazy(() => import('./NotFound.tsx'));
const Signin = lazy(() => import('./Signin.tsx'));
const PostBrowser = lazy(() => import('./PostBrowser.tsx'));
const PostEditor = lazy(() => import('./PostEditor.tsx'));
const QuoteEditor = lazy(() => import('./QuoteEditor.tsx'));
const FileManage = lazy(() => import('./FileManage.tsx'));

// 在应用初始化时配置
message.config({
    // 消息容器优先 编辑器 其次 root 最后 body
    getContainer: () => {
        return document.getElementById('post_editor')
            || document.getElementById('root')
            || document.body;
    },
    top: 64,  // 可调整距离顶部的距离
});

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
                    message.success({content: '已将文本进行美化排版。'})
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
    const {pathname} = useLocation();
    const device = detectDevice();
    const [notPCModalOpen, setNotPCModalOpen] = useState(false);

    // useLayoutEffect 在 DOM 更新前执行，更流畅
    // 页面跳转前，重置窗口滚动到初始位置
    useLayoutEffect(() => {
        window.scrollTo({
            top: 0,
            left: 0,
            behavior: 'smooth' // 可选：平滑滚动
        });
    }, [pathname]);

    useEffect(() => {
        // 依赖静态值表示仅初始化时调用一次
        UrlHelper.init(navigate);
        UserClient.init(ClientScope.WEB);

        if (!device.isDesktop) {
            const notPCNoticeForbiddenFlag = StorageHelper.get<string>(StorageKey.NOT_PC_NOTICE_FORBIDDEN);
            if (!notPCNoticeForbiddenFlag) {
                setNotPCModalOpen(true);
            }
        }
    }, []);

    return (
        <QueryClientProvider client={queryClient}>
            <ConfigProvider locale={zhCN}>
                <Modal key={'notPCModal'}
                       title='提示：'
                       open={notPCModalOpen}
                       onOk={(event) => {
                           StorageHelper.set(StorageKey.NOT_PC_NOTICE_FORBIDDEN, 'FORBIDDEN');
                           setNotPCModalOpen(false);
                       }}
                       confirmLoading={false}
                       okText='确认，且不再提醒'
                       cancelText='取消'
                       onCancel={(event) => {
                           setNotPCModalOpen(false);
                       }}
                >
                    <p>检测到您使用的访问设备非 PC 环境，本站原定使用场景非移动端，如继续访问可能会存在交互界面不适配问题，尽请见谅~，推荐使用 PC 访问，移动端也建议使用浏览器的电脑模式。</p>
                </Modal>
                <Routes>
                    <Route path={'/post/:pid'} element={<PostBrowser key={'PostBrowser'}/>}/>
                    <Route path={'/manage/editor/post'} element={<PostEditorContextProvider><PostEditor key={'PostEditor'}/></PostEditorContextProvider>}/>
                    <Route path={'/manage/editor/quote'} element={<QuoteEditorContextProvider><QuoteEditor key={'QuoteEditor'}/></QuoteEditorContextProvider>}/>
                    <Route path={'/manage/file/glance'} element={<FileManage key={'FileManage'}/>}/>
                    <Route path={'/manage/file/operate'} element={<NotFound key={'NotFound'} delayTime={3000}/>}/>
                    <Route path={'/signup'} element={<NotFound key={'NotFound'} delayTime={3000}/>}/>
                    <Route path={'/signin'} element={<Signin key={'Signin'}/>}/>
                    <Route path={'/'} element={<HomeProvider><Home key={'Home'}/></HomeProvider>}/>
                    {/*从上到下匹配，上方全未匹配命中则说明 404 */}
                    <Route path={'*'} element={<NotFound key={'NotFound'} delayTime={3000}/>}/>
                </Routes>
            </ConfigProvider>
        </QueryClientProvider>
    );
}
