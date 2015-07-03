import json
from urllib2 import Request, urlopen, URLError

api_key = '3F0697355EF5230B'


def convert_xml_to_json(data):
    pass


def get_series_id(series_name):
    tvbd_req = Request(
        'http://thetvdb.com/api/' +
        api_key +
        '/GetSeries.php?seriesname=' +
        series_name)
    try:
        response = urlopen(tvbd_req)
        print response
        tvbd_xml_data = response.read()
        print tvbd_xml_data
    except URLError:
        'URL is invalid.'


def get_series_episode(series_id, season_number, episode_number):
    pass

get_series_id('Supernatural')