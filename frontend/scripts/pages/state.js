var React = require('react');
var {Navigation, State} = require('react-router');

var State = React.createClass({
    mixins: [Navigation, State],

    stateSelect(e){
    	var st = e.target.value;
    	console.log(st);
    	this.transitionTo("schooling", {year: this.getParams().year, state: st})
    },
    render(){
        return(
            <div className="state">
                <select ref="blah" onChange={this.stateSelect}>
                    <option value="qld">Queensland</option>
                    <option value="nt">Northern Territory</option>
                    <option value="wa">Western Australia</option>
                    <option value="sa">South Australia</option>
                    <option value="nsw">New South Wales</option>
                    <option value="tas">Tasmania</option>
                    <option value="act">Australian Capital Territory</option>
                    <option value="vic">Victoria</option>
                </select>
            </div>
        )
    }
});

module.exports = State;
