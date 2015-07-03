#!/usr/bin/env python
import json
import requests
import xml.etree.ElementTree as ET
from optparse import OptionParser
from local import API_KEY
from xml2json import xml2json


def get_series_id(series_name):
    tvbd_request = requests.get(
        'http://thetvdb.com/api/' +
        'GetSeries.php?seriesname=' +
        str(series_name))

    response = tvbd_request.content
    tree = ET.fromstring(response)
    series = tree.find('Series')
    series_id = series.find('seriesid')
    return series_id.text


def return_json(data):
    data = xml2json(data)
    return data


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


class ParserOptions():
    parser = OptionParser(
        description="Pulls data from tvdb!",
        prog='tvdb_data',
        usage='./%prog.py -n series_name -s season_num -e episode_num '
        )
    parser.add_option("-n", "--name", action='store', dest='name')
    parser.add_option("-s", "--season", action='store', dest='season')
    parser.add_option("-e", "--episode", action='store', dest='episode')

    options, arguments = parser.parse_args()

    name = str(options.name)
    season = str(options.season)
    episode = str(options.episode)
    get_series_episode(name, season, episode)
