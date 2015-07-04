var React = require('react');

var Schooling = React.createClass({
    render(){
        return(
            <div className="schooling">
                <button className="primary">Primary</button>
                <button className="secondary">Secondary</button>
            </div>
        );
    }
});

module.exports = Schooling;
