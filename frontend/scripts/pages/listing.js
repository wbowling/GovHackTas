var React = require('react');
var { State } = require('react-router');
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
                <input defaultValue={this.state.year} min="1980" max="2005" type="range" step="1" ref="yearSlider" onChange={this.sliderMove} onMouseUp={this.sliderFinished}/>
                { listingItems }

            </div>
        );
    }
});

module.exports = Listing;
