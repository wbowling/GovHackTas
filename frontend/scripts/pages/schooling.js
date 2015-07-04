var React = require('react');
var {Navigation, State, Link} = require('react-router');

var Schooling = React.createClass({
	mixins: [Navigation, State],
	toList(e) {
	    var year = parseInt(this.getParams().year);
    	var state = this.getParams().state;
    	var skool = e.target.value;
		if(skool == "primary"){
			year += 7;
		}else{
			year += 14;
		}
    	this.transitionTo("listing", {year: year, state: state});
	},
    render(){
        return(
            <div className="schooling">
                <div value="primary" onClick={this.toList} className="primary">Primary</div>
            	<div value="secondary" onClick={this.toList} className="secondary">Secondary</div>
            </div>
        );
    }
});

module.exports = Schooling;
