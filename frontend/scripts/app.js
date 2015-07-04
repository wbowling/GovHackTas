var React = require('react');
var RouteHandler = require('react-router').RouteHandler;

var App = React.createClass({
  render() {
    return (
      <div className="body">
         <RouteHandler />
      </div>
    );
  }
});

module.exports = App;
