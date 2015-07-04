var React = require('react');
var { State } = require('react-router');
var backend = require('./../backend');


var Listing = React.createClass({
    mixins: [State],

    backendCallback(data) {
        debugger;
    },

    componentWillMount(){
        var { year, state } = this.getParams();
        backend.getList(year, state, this.backendCallback);
    },

    render(){
        return(
            <div >
                { this.state }
            </div>
        );
    }
});

module.exports = Listing;
