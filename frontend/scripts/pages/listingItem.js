var React = require('react');
var ShowInfo = require('./showInfo.js');
var backend = require('./../backend');

var ListingItem = React.createClass({
    getInitialState(){
        return{
            closed: true,
            showInfo: null,
            thinking: false
        }
    },

    toggle() {
        var closed = this.state.closed;
        var thinking = false;
        if(this.state.showInfo === null){
            this.fetchShowInfo();
            thinking = true;
        }
        this.setState({
            closed: !closed,
            thinking: thinking
        })
    },

    backendCallback(data) {
        this.setState({
            showInfo: data,
            thinking: false
        })
    },

    fetchShowInfo(){
        backend.getShowInfo(this.props.showName, this.backendCallback)

    },

    render() {
        var contents;
        if( this.state.thinking)
        {
            contents = <img className="loading" src="../../assets/loading.gif" />;
        }
        else if( !this.state.closed && this.state.showInfo)
        {
            contents = <ShowInfo data={this.state.showInfo} />
        }else{
            contents = "";
        }

        return(
            <div className="listing-item" onClick={this.toggle}>
                {this.props.showName} {this.props.count}
                {contents}
            </div>
        )
    }
});

module.exports = ListingItem;
