// 内置插件
const path = require("path");

// 外部插件
const HtmlWebpackPlugin = require("html-webpack-plugin");
const {CleanWebpackPlugin} = require("clean-webpack-plugin");
const CompressionPlugin = require("compression-webpack-plugin");
const CopyPlugin = require("copy-webpack-plugin");
const BundleAnalyzerPlugin = require('webpack-bundle-analyzer').BundleAnalyzerPlugin;

var config = {
    // 指定下列依赖从远端获取，不打包进 .js 文件
    externals: {
        // "prettier": "prettier"
        // "react": "React",
        // "react-dom": "ReactDOM"
        // "vditor": "Vditor"
    },
    // 添加需要解析的文件格式(import 时不需要再标注下列尾缀)
    resolve: {
        extensions: ['.ts', '.tsx', '.js', '.json']
    },
    // mode: 'development', // development,production
    //入口文件的路径(可配多个，此处只配置了 "index" 实体)
    entry: {
        index: "./src/tsx/app.tsx"
    },
    output: {
        publicPath: "/",
        path: path.join(__dirname, "./dist"),
        filename: "./js/[name]-[chunkhash].js"
    },
    devServer: {
        static: path.join(__dirname, "./dist"),
        historyApiFallback: true,
        compress: true,
        open: true,
        port: 9000,
        // host: "192.168.18.12"
    },
    module: {
        rules: [
            {
                test: /\.tsx?$/,
                exclude: /node_modules/,
                loader: 'babel-loader',
                options: {
                    presets: [
                        '@babel/preset-env',
                        '@babel/preset-react',
                        '@babel/preset-typescript'
                    ]
                }
            },
            {
                test: /\.css?$/,
                use: [
                    {loader: "style-loader"},
                    {loader: "css-loader"}
                ],
                // exclude: /node_modules/,
                // include: path.resolve(__dirname, "src")
            },
            {
                test: /\.less?$/,
                use: [
                    {loader: 'style-loader'},
                    {loader: 'css-loader'},
                    {loader: 'less-loader'}
                ],
                // exclude: /node_modules/,
                // include: path.resolve(__dirname, "src")
            },
            {
                test: /\.s[ac]ss$/i,
                use: [
                    // Creates `style` nodes from JS strings
                    {loader: "style-loader"},
                    // Translates CSS into CommonJS
                    {loader: "css-loader"},
                    // Compiles Sass to CSS
                    {loader: "sass-loader"},
                ],
            }
        ]
    },
    plugins: [
        new CleanWebpackPlugin(
            {
                cleanOnceBeforeBuildPatterns: [
                    path.join(__dirname, "dist")
                ]
            }
        ),
        new HtmlWebpackPlugin({
            filename: "index.html",
            title: "我的小宅子",
            favicon: "./src/img/favicon.ico",
            template: "./src/html/defaultTemplate.html",
            chunks: ["index", "commons"],
            inject: "body",
            minify: {
                removeComments: true,
                collapseWhitespace: true
            }
        }),
        new CompressionPlugin({
            // 压缩类型
            algorithm: "gzip",
            // 被压缩目标的正则匹配规则
            test: /\.(js|css)$/,
            // 字节数 只处理比这个大的资源(100 kb)
            threshold: 102400,
            // 压缩率 只有比这个小的才会处理
            minRatio: 0.8
        }),
        new BundleAnalyzerPlugin()
    ],
    optimization: {
        splitChunks: {
            // 打包公共依赖
            // chunks: "all",
            // name: "commons"
        }
    }
};

module.exports = (env, argv) => {
    if (argv.mode === 'development') {
        config.devtool = 'source-map';
    }

    return config;
};