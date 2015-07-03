import json
import requests
import xml.etree.ElementTree as ET
import StringIO
from local import API_KEY
from xml2json import xml2json


def get_series_id(series_name):
    tvbd_request = requests.get(
        'http://thetvdb.com/api/' +
        'GetSeries.php?seriesname=' +
        series_name)

    response = tvbd_request.content
    tree = ET.fromstring(response)
    series = tree.find('Series')
    series_id = series.find('seriesid')
    return series_id.text


def return_json(data):
    data = xml2json(data)
    print data


def get_series_episode(series_name, se, ep):
    series_id = get_series_id(series_name)
    tvbd_request = requests.get(
        'http://thetvdb.com/api/' +
        API_KEY +
        '/series/' +
        series_id +
        '/default/' +
        str(se) +
        '/' +
        str(ep))
    response = tvbd_request.content
    print return_json(response)

get_series_episode('Supernatural', 1, 3)
