var React = require('react');
var {Router} = require('react-router');
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
                <button className="year--control up" onMouseDown={this.addYear} onMouseUp={this.mouseUp}>&#x25B2;</button>
                <input className="year--input" disabled="disabled" type="number" min="1930" max="1999" value={this.state.year}></input>
                <button className="year--control down" onMouseDown={this.takeYear} onMouseUp={this.mouseUp}>&#x25BC;</button>
                <Link to="state" params={{year: this.state.year}}>Pick State</Link>
            </div>
        );
    }
});

module.exports = Year;
