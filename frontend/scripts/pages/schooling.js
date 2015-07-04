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
                <button value="primary" onClick={this.toList} className="primary">Primary</button>
            	<button value="secondary" onClick={this.toList} className="secondary">Secondary</button>
            </div>
        );
    }
});

module.exports = Schooling;
