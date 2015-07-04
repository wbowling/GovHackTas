var React = require('react');
var Navigation = require('react-router').Navigation;

var State = React.createClass({
    mixins: [Navigation],

    stateSelect(){
        this.transitionTo('schooling');
    },

    render(){
        return(
            <div className="state">
                <select onChange={this.stateSelect}>
                    <option name="qld">Queensland</option>
                    <option name="nt">Northern Territory</option>
                    <option name="wa">Western Australia</option>
                    <option name="nsw">New South Wales</option>
                    <option name="tas">Tasmania</option>
                    <option name="act">Australian Capital Territory</option>
                    <option name="vic">Victoria</option>
                </select>
            </div>
        )
    }
});

module.exports = State;
