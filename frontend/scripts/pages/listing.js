var React = require('react');
var { State } = require('react-router');
var backend = require('./../backend');

var ListingItem = require('./listingItem');

var Listing = React.createClass({
    mixins: [State],
    getInitialState(){
        return{
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

    render(){
        var listingItems = this.state.showList.map((show, index) =>{
            return (
                <ListingItem key={index} showName={ show["name"] } />
            )
        });

        return(
            <div className="listing">
                { listingItems }
            </div>
        );
    }
});

module.exports = Listing;

var fakeShowListing = {
    "shows": [{
    "name": "Play School",
    "count": 253
  }, {
    "name": "Heartbreak High",
    "count": 150
  }, {
    "name": "Feral TV",
    "count": 107
  }, {
    "name": "Trap Door, The",
    "count": 100
  }, {
    "name": "Secret World Of Alex Mack, The",
    "count": 91
  }, {
    "name": "Sunday Afternoon With Andrea Stretton",
    "count": 91
  }, {
    "name": "Wildlife",
    "count": 91
  }, {
    "name": "Gardening Australia",
    "count": 84
  }, {
    "name": "Sesame Street",
    "count": 83
  }]
}
