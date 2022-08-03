// 内置插件
const path = require("path");

// 外部插件
const HtmlWebpackPlugin = require("html-webpack-plugin");
const {CleanWebpackPlugin} = require("clean-webpack-plugin");
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
    mode: 'development',
    //入口文件的路径(可配多个，此处只配置了 "index" 实体)
    entry: {
        index: "./src/tsx/index.tsx"
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
            title: "骨架模板页",
            favicon: "./src/img/favicon.ico",
            template: "./src/html/defaultTemplate.html",
            chunks: ["index", "commons"],
            inject: "body",
            minify: {
                removeComments: true,
                collapseWhitespace: true
            }
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