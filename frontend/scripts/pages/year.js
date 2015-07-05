var React = require('react');
var {Router} = require('react-router');
var Link = require('react-router').Link;

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
        var newYear = up ? (this.state.year + 1) : (this.state.year -1);
        if(newYear > 1977 && newYear < 2012)
        {
            this.setState({
                year: newYear
            });
        }

    },

    render() {
        return (
            <div className="year">
                <button className="year--control up" onClick={this.addYear}>&#x25B2;</button>
                <input className="year--input" disabled="disabled" type="number" min="1930" max="1999" value={this.state.year}></input>
                <button className="year--control down" onClick={this.takeYear}>&#x25BC;</button>
                <Link className="year--nav" to="state" params={{year: this.state.year}}><i className="fa fa-map-marker"></i>Pick State</Link>
            </div>
        );
    }
});

module.exports = Year;
