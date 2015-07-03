import json
import requests
import xml.etree.ElementTree as ET
import StringIO
from local import API_KEY

api_key = API_KEY


def convert_xml_to_json(data):
    pass


def get_series_id(series_name):
    tvbd_request = requests.get(
        'http://thetvdb.com/api/' +
        'GetSeries.php?seriesname=' +
        series_name)

    response = tvbd_request.content
    tree = ET.fromstring(response)
    series = tree.find('Series')
    series_id = series.find('seriesid')
    return series_id


def get_series_episode(series_id, season_number, episode_number):
    get_series_id('Supernatural')
    pass
