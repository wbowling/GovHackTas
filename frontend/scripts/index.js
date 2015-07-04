var React = require('react');
var App = require('./app');
var Router = require('react-router');
var Route = Router.Route;
var DefaultRoute = Router.DefaultRoute;

//Pages
var Year = require('./pages/year');
var State = require('./pages/state');
var Schooling = require('./pages/schooling');

Router.run((

    <Route path="/" handler={App}>
        <DefaultRoute name="Year" handler={Year} path="year" />
        <Route name="state" handler={State} path="state/:year" />
        <Route name="schooling" handler={Schooling} path="schooling/:state/:year" />
    </Route>
), Handler => {
    React.render(<Handler/>, document.getElementById('content'))
});
