import json
import requests

api_key = '3F0697355EF5230B'


def convert_xml_to_json(data):
    pass


def get_series_id(series_name):
    tvbd_request = requests.get(
        'http://thetvdb.com/api/' +
        'GetSeries.php?seriesname=' +
        series_name)

    print tvbd_request
    response = tvbd_request.content
    print response


def get_series_episode(series_id, season_number, episode_number):
    pass

get_series_id('Supernatural')
