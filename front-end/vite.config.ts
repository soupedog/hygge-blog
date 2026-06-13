import {defineConfig} from 'vite'
import react, {reactCompilerPreset} from '@vitejs/plugin-react'
import babel from '@rolldown/plugin-babel'
import viteCompression from 'vite-plugin-compression'

// https://vite.dev/config/
export default defineConfig({
    plugins: [
        react({
            jsxRuntime: 'automatic',  // 关键：启用自动 JSX 运行时
            jsxImportSource: 'react'   // 可选，默认就是 react
        }),
        babel({presets: [reactCompilerPreset()]}),
        viteCompression({
            verbose: true,
            threshold: 10240, // 10 kb 以上的才进行压缩
            algorithm: 'gzip',
            ext: '.gz',
            deleteOriginFile: false   // 如果为 true 压缩后删除源文件，只保留 .gz
        })
    ],
})
