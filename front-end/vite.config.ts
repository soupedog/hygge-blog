import { defineConfig } from 'vite'
import react, { reactCompilerPreset } from '@vitejs/plugin-react'
import babel from '@rolldown/plugin-babel'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    react({
        jsxRuntime: 'automatic',  // 关键：启用自动 JSX 运行时
        jsxImportSource: 'react'   // 可选，默认就是 react
    }),
    babel({ presets: [reactCompilerPreset()] })
  ],
})
