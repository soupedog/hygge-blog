# 初始化项目命令

**node 环境我就不多提了，这个你得先准备好(即可以使用 npm)**

```
// 创建 package.json   一路回车，如果有什么想配置的自行补充
npm init

// 安装 typescript
npm install typescript --save-dev

// 创建 typescript 配置文件
tsc --init

// 安装 webpack
npm install webpack webpack-cli webpack-dev-server --save-dev

// 安装 react
npm install react @types/react react-dom @types/react-dom react-router-dom --save-dev

// 安装 babel
npm install babel-loader @babel/core @babel/preset-env @babel/preset-react @babel/preset-typescript @babel/plugin-proposal-class-properties --save-dev

// 安装各种 css loader
npm install css-loader style-loader sass sass-loader less less-loader --save-dev

// 安装实用插件
npm install webpack-bundle-analyzer html-webpack-plugin clean-webpack-plugin --save-dev

// 压缩插件
npm install compression-webpack-plugin --save-dev

// 好用的 js 音乐播放器
npm install aplayer

// 多条件合并返回 true false 工具
npm install clsx

// querystring 工具
npm install qs --save-dev

// antd
npm install antd

// md 编辑工具
npm install vditor

// http 请求工具
npm install axios

// 按需加载插件
npm install babel-plugin-import --save-dev

// 安装 jquery
npm install jquery @types/jquery --save-dev

// 安装文本格式化工具
npm install prettier
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