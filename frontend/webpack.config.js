var webpack = require('webpack');

module.exports = {
  devtool: '#inline-source-map',
  entry:[
    'webpack-dev-server/client?http://0.0.0.0:3000', // WebpackDevServer host and port
    'webpack/hot/only-dev-server',
    './scripts/index'
  ],
  output: {
    path: __dirname + '/public/',
    filename: 'bundle.js',
    publicPath: '/public/'
  },
  module: {
    loaders: [
      { test: /\.html$/, loader:'html-loader'},
      { test: /\.js$/, loaders: ['react-hot', 'jsx?harmony'], exclude: /node_modules/ }
    ]
  },
  resolve: {
    extensions: ['', '.js']
  },
  plugins: [
    new webpack.HotModuleReplacementPlugin(),
    new webpack.NoErrorsPlugin()
  ]
};
