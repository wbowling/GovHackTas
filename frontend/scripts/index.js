var React = require('react');
var App = require('./app');
var Router = require('react-router');
var Route = Router.Route;
var DefaultRoute = Router.DefaultRoute;

//Pages
var Year = require('./pages/year');
var State = require('./pages/state');
var Listing = require('./pages/listing');

Router.run((

    <Route path="/" handler={App}>
        <DefaultRoute name="Year" handler={Year} />
        <Route name="state" handler={State} path="state/:year" />
        <Route name="listing" handler={Listing} path="listing/:state/:year" />
    </Route>
), Handler => {
    React.render(<Handler/>, document.getElementById('content'))
});
