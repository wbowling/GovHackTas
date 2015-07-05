#!/usr/bin/python
import requests
import urllib
import xml.etree.ElementTree as ET
from optparse import OptionParser
from local import TVDB_API_KEY
from xml2json import xml2json


def return_json(data):
    data = xml2json(data)
    return data


def get_series_id(series_name):
    tvbd_request_str = (
        'http://thetvdb.com/api/' +
        'GetSeries.php?seriesname=' +
        str(urllib.quote(series_name)))

    tvbd_request = requests.get(tvbd_request_str)

    response = tvbd_request.content
    tree = ET.fromstring(response)
    series = tree.find('Series')
    if series is not None:
        series_id = series.find('seriesid')
        return series_id.text
    else:
        print 'Query returned nothing'


def get_series_info(series_name):
    tvbd_request_str = (
        'http://thetvdb.com/api/' +
        'GetSeries.php?seriesname=' +
        str(urllib.quote(series_name)))

    tvbd_request = requests.get(tvbd_request_str)

    response = tvbd_request.content
    print return_json(response)
    '''
    tree = ET.fromstring(response)
    series = tree.find('Series')
    if series is not None:
        overview = series.find('Overview')
        print overview.text
    else:
        print 'Query returned nothing'
    '''


def get_series_episode(series_name, se, ep):
    series_id = get_series_id(series_name)

    if series_id is not None:
        tvbd_request = requests.get(
            'http://thetvdb.com/api/' +
            TVDB_API_KEY +
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
        usage='./%prog.py -n "series_name" -s season_num -e episode_num '
        )
    parser.add_option("-n", "--name", action='store', dest='name', help='Wrapped in quotes')
    parser.add_option("-s", "--season", action='store', dest='season')
    parser.add_option("-e", "--episode", action='store', dest='episode')

    options, arguments = parser.parse_args()

    if not options.name:
        parser.error('Series name not given.')

    name = str(options.name.title())
    season = str(options.season)
    episode = str(options.episode)
    # TODO FIX HACK BELOW
    if len(season) == 4:
        # TODO FIX HACK ABOVE
        get_series_info(name)
    else:
        get_series_episode(name, season, episode)
