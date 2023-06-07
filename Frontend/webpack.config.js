const path = require('path');


module.exports = {
  mode: 'development',
  optimization: {
    usedExports: true
  },
  entry: {
    profilePage: path.resolve(__dirname, 'public/javascript/pages/profilePage.js'),
    indexPage: path.resolve(__dirname, 'public/javascript/pages/indexPage.js'),
    searchPage: path.resolve(__dirname, 'public/javascript/pages/searchPage.js'),
    recipePage: path.resolve(__dirname, 'public/javascript/pages/recipePage.js'),
  },
  output: {
    path: path.resolve(__dirname, 'dist'),
    filename: '[name].bundle.js',
    publicPath: '/',
  },
  module: {
    rules: [
      {
        test: /\.js$/,
        exclude: /node_modules/,
        use: {
          loader: 'babel-loader',
          options: {
            presets: ['@babel/preset-env']
          }
        }
      }
    ]
  },
  target: "web",
  devServer: {
    https: false,
    port: 8080,
    open: true,
    openPage: 'http://localhost:8080',
    // diableHostChecks, otherwise we get an error about headers and the page won't render
    disableHostCheck: true,
    contentBase: 'packaging_additional_published_artifacts',
    // overlay shows a full-screen overlay in the browser when there are compiler errors or warnings
    overlay: true,
    proxy: [
      {
        context: [
          '/',
        ],
        target: 'http://localhost:3000'
      }
    ]
  },
  plugins: [

  ]
}
