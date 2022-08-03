# 初始化项目命令

**node 环境我就不多提了，这个你得先准备好(即可以使用 npm)**

```
// 创建 package.json   一路回车，如果有什么想配置的自行补充
npm init

// 安装 typescript
npm i typescript --save-dev

// 创建 typescript 配置文件
tsc --init

// 安装 webpack
npm i webpack webpack-cli webpack-dev-server --save-dev

// 安装 react
npm i react @types/react react-dom @types/react-dom --save-dev

// 安装 babel
npm i babel-loader @babel/core @babel/preset-env @babel/preset-react @babel/preset-typescript --save-dev

// 安装各种 css loader
npm i css-loader style-loader sass sass-loader less less-loader --save-dev

// 安装实用插件
npm i webpack-bundle-analyzer html-webpack-plugin clean-webpack-plugin --save-dev

// 好用的 js 音乐播放器
npm i aplayer

// 多条件合并返回 true false 工具
npm i clsx

// antd
npm i antd

// md 编辑工具
npm i vditor

// http 请求工具
npm i axios

```

## 安装完 webpack 后需要添加配置文件 ``webpack.config.js``

和 ``package.json`` 位于同一目录，这是最初默认样子，后续会做许多插件等其他配置

```
const path = require('path');

module.exports = {
  entry: './src/index.js',
  output: {
    filename: 'main.js',
    path: path.resolve(__dirname, 'dist'),
  },
};
```

## 谷歌浏览器插件

``React Developer Tools``