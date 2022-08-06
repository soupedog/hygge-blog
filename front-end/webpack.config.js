// 内置插件
const path = require("path");

// 外部插件
const HtmlWebpackPlugin = require("html-webpack-plugin");
const {CleanWebpackPlugin} = require("clean-webpack-plugin");
const CompressionPlugin = require("compression-webpack-plugin");
const BundleAnalyzerPlugin = require('webpack-bundle-analyzer').BundleAnalyzerPlugin;

module.exports = {
    // 指定下列依赖从远端获取，不打包进 .js 文件
    // externals: {
    //     "react": "React",
    //     "react-dom": "ReactDOM"
    // },
    // 添加需要解析的文件格式(import 时不需要再标注下列尾缀)
    resolve: {
        extensions: ['.ts', '.tsx', '.js', '.json']
    },
    mode: 'development', // development,production
    devtool: 'eval-source-map',
    //入口文件的路径(可配多个，此处只配置了 "index" 实体)
    entry: {
        index: "./src/tsx/app.tsx"
    },
    output: {
        publicPath: "",
        path: path.resolve(__dirname, "./dist"),
        filename: "./js/[name]-[chunkhash].js"
    },
    devServer: {
        static: path.join(__dirname, "./dist"),
        open: true,
        compress: true,
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
                    ],
                    plugins: [
                        // 支持装饰器(类似 java 的注解)
                        ["@babel/plugin-proposal-decorators", { "legacy": true }],
                        ["@babel/plugin-proposal-class-properties", { "loose" : true }]
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
                    path.resolve(__dirname, "dist")
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
            algorithm: 'gzip', // 类型
            test: /\.(js|css)$/, // 匹配规则
            threshold: 10240, // 字节数 只处理比这个大的资源
            minRatio: 0.8 // 压缩率 只有比这个小的才会处理
        }),
        new BundleAnalyzerPlugin()
    ],
    optimization: {
        splitChunks: {
            // 打包公共依赖
            chunks: "all",
            name: "commons"
        }
    }
}