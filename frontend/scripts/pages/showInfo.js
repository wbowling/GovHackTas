var React = require('react');
var Youtube = require('react-youtube');

var ShowInfo = React.createClass({
    getInitialState(){

        data = this.props.data;
        var summary = JSON.parse(data.summary);
        var seriesInfo = summary["Data"]["Series"];
        return {
            overview: seriesInfo["Overview"],
            banner: "http://www.thetvdb.com/banners/" + seriesInfo["banner"],
            youtubeUrl: "https://www.youtube.com/watch?v=" + data.youtubeId
        }
    },


    render() {
        return (
            <div className="listing-item--content">
                { this.state.banner ? <img src={this.state.banner} /> : '' }
                <p>{this.state.overview}</p>
                <Youtube url={this.state.youtubeUrl} />
            </div>
        )
    }
})

module.exports = ShowInfo;
