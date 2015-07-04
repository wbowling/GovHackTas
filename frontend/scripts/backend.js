//var fetch = require("whatwg-fetch");

var endpoint = "http://localhost:8080/airings";
var hourParam = "&starthour=15&endhour=19";

var backend = {
    getList: function(year, state, cb){
        fetch(`${endpoint}?year=${year}&state=${state}${hourParam}`)
            .then( data => data.json() )
            .then(
                cb(json))
            .catch(console.log('ooooh shiiiet'));
    }
}

module.exports = backend;
