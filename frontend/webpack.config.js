const path = require('path');

module.exports = {
    mode: "development",
    entry: [
        "./src/index.tsx",
        "./public/index.html"
    ],
    output: {
        filename: "[name].bundle.js",
        path: path.resolve(__dirname, 'dist')
    },
    devtool: 'source-map',
    resolve: {
        extensions: ['.js', '.json', '.ts', '.tsx']
    },
    module: {
        rules: [
            {
                test: /\.(ts|tsx)$/,
                loader: "awesome-typescript-loader"
            },
            {
                test: /\.html/,
                loader: 'file-loader?name=[name].[ext]',
            }
        ]
    }
};
