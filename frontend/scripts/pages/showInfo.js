var React = require('react');
var Youtube = require('react-youtube');

var ShowInfo = React.createClass({
    getInitialState(){

        data = this.props.data;

        var summary = JSON.parse(data.summary);

        if(summary["Data"] == null)
        {
            return { nothing: true };
        }

        var seriesInfo = summary["Data"]["Series"];


        if (seriesInfo.constructor === Array)
        {
            seriesInfo = seriesInfo[0];
        }

        return {
            overview: seriesInfo["Overview"],
            banner: "http://www.thetvdb.com/banners/" + seriesInfo["banner"],
            youtubeUrl: "https://www.youtube.com/watch?v=" + data.youtubeId

        }
    },


    render() {
        if (this.state.nothing)
        {
            return (
                <div className='listing-item--no-content'>
                    <p>Sorry, no info found, would you like to <a href='/'>contribute</a>?</p>
                </div>)
        }
        else{
            return (
                <div className="listing-item--content">
                    <img src={this.state.banner} />
                    <p>{this.state.overview}</p>
                    <Youtube url={this.state.youtubeUrl} />
                </div>
            )
        }
    }
})

module.exports = ShowInfo;
