//var fetch = require("whatwg-fetch");

var endpoint = "http://localhost:8080";
var hourParam = "&starthour=15&endhour=19";

var backend = {
    getList: function(year, state, cb){
        fetch(`${endpoint}/airings?year=${year}&state=${state}${hourParam}`)
            .then( data => data.json())
            .then( json => cb(json))
            .catch(error => console.log(error));
    },

    getShowInfo: function(show, cb){
        fetch(`${endpoint}/details?series=${show}`)
            .then( data => data.json())
            .then( json => cb(json))
            .catch( error => console.log(error));
    }
}

module.exports = backend;
