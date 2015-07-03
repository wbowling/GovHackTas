var React = require('react');
var Link = require('react-router').Link;
var interval;

var Year = React.createClass({

    getInitialState() {
        return {
            year: 1985
        };
    },

    addYear(){
        this.alterYear(true);
    },

    takeYear(){
        this.alterYear(false);
    },

    alterYear(up){
        interval = setInterval(function(){
            var currentYear = this.state.year;
            if(up){
                currentYear++;
            }else{
                currentYear--;
            }
            //TODO upper lower bounds
            this.setState({
                year: currentYear
            });
        }.bind(this), 100);
    },

    mouseUp(){
        clearInterval(interval);
    },

    render() {
        return (
            <div className="year">
                <button onMouseDown={this.addYear} onMouseUp={this.mouseUp}>Up</button>
                <input className="year--input" disabled="disabled" type="number" min="1930" max="1999" value={this.state.year}></input>
                <button onMouseDown={this.takeYear} onMouseUp={this.mouseUp}>Down</button>
                <Link to="state">Pick State</Link>
            </div>
        );
    }
});

module.exports = Year;
