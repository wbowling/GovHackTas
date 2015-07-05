var React = require('react');
var { State, Link } = require('react-router');
var backend = require('./../backend');

var ListingItem = require('./listingItem');


var Listing = React.createClass({
    mixins: [State],
    getInitialState(){
        return{
            year: this.getParams().year,
            showList: []
        }
    },

    backendCallback(data) {
        this.setState({
            showList: data.shows
        })
    },

    componentWillMount(){
        var { year, state } = this.getParams();
        backend.getList(year, state, this.backendCallback);
    },

    sliderFinished(e){
        var { state } = this.getParams();
        backend.getList(e.target.value, state, this.backendCallback);
    },

    sliderMove(e){
        this.setState({
            year: e.target.value
        })
    },
    addYear(e){
        if (this.state.year < 2012) {
        this.setState({
            year: parseInt(this.state.year) + 1
        })
        var slider = this.refs.yearSlider.getDOMNode()
        slider.value = this.state.year
        this.sliderFinished({target:slider})
    }},

    removeYear(e){
        if (this.state.year > 1977) {
        this.setState({
            year: parseInt(this.state.year) - 1
        })
        var slider = this.refs.yearSlider.getDOMNode()
        slider.value = this.state.year
        this.sliderFinished({target:slider})
    }},

    shouldComponentUpdate(nextProps, nextState){
        return true;//this.state.showList !== nextState.showList;
    },

    render(){
        var listingItems = this.state.showList.map((show, index) =>{
            return (
                <ListingItem key={index} count={show.count} showName={ show.name } />
            )
        });

        return(
            <div className="listing">
                <span className="listing--year">{this.state.year}</span>
                <br/>
                <button className='listing--control' onClick={this.removeYear}>&#x25C0;</button>
                <input defaultValue={this.state.year} min="1978" max="2011" type="range" step="1" ref="yearSlider" onChange={this.sliderMove} onMouseUp={this.sliderFinished}/>
                <button className='listing--control' onClick={this.addYear}>&#x25B6;</button>
                <Link to='Year'><i className="fa fa-home"></i></Link>
                { listingItems }

            </div>
        );
    }
});

module.exports = Listing;
