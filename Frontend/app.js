var express = require('express');
var path = require('path');
var cookieParser = require('cookie-parser');
var logger = require('morgan');
const webpack = require('webpack');
const webpackDevMiddleware = require('webpack-dev-middleware');
const webpackConfig = require('./webpack.config.js');
const { createProxyMiddleware, fixRequestBody} = require('http-proxy-middleware');

var app = express();
const compiler = webpack(webpackConfig);

app.use(webpackDevMiddleware(compiler, {
    writeToDisk: false,
    publicPath: webpackConfig.output.publicPath
}));

app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cookieParser());
app.engine('html', require('ejs').renderFile);
app.set('view engine', 'html');
app.set('views', path.join(__dirname, 'public/views'));
app.use(express.static(path.join(__dirname, 'public')));

const { auth } = require('express-openid-connect');

const config = {
    authRequired: false,
    auth0Logout: true,
    secret: process.env.AUTH_CLIENT_SECRET,
    baseURL: 'http://localhost:3000',
    clientID: process.env.AUTH_CLIENT_ID,
    issuerBaseURL: process.env.AUTH_ISSUER_URI
};

// auth router attaches /login, /logout, and /callback routes to the baseURL
app.use(auth(config));

const { requiresAuth } = require('express-openid-connect');

app.get('/', function(req, res, next) {
    res.render("index",{
        isAuthenticated: req.oidc.isAuthenticated()
    });
});
app.get('/profile', requiresAuth(), function(req, res, next) {
    res.render("profile", {
        isAuthenticated: req.oidc.isAuthenticated(),
        userId: req.oidc.user.nickname,
    });
});
app.get('/search', function(req, res, next) {
    res.render("search",{ isAuthenticated: req.oidc.isAuthenticated()});
});
app.get('/recipe/:recipeName', function(req, res, next) {
    res.render("recipe", {
        isAuthenticated: req.oidc.isAuthenticated(),
        userId: req.oidc.isAuthenticated() ? req.oidc.user.nickname : null,
    });
});


// TODO: change this line to the url of the elastic beanstalk instance
const apiProxy = createProxyMiddleware('/api', {
    target: 'http://localhost:5001',
    changeOrigin: true,
    onProxyReq: fixRequestBody,
});

app.use('/api', apiProxy);

app.listen(3000);



module.exports = app;
