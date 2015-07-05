#!/usr/bin/python
import requests
import json
import urllib
from optparse import OptionParser
from local import YOUTUBE_API_KEY


def get_first_video(name):
    youtube_search = requests.get(
        'https://www.googleapis.com/youtube/v3/search?part=snippet' +
        '&type=video' +
        '&q=' + str(urllib.quote(name)) +
        '&maxResults=1' +
        # '&topicId=/m/01xrzjs ' TODO Make this work
        '&key=' + YOUTUBE_API_KEY)

    reponse = youtube_search.content
    json_response = json.loads(reponse)
    items = json_response['items']
    if len(items) > 0:
        video_id = items[0]['id']['videoId']
        print video_id
    else:
        print "No results found"


def get_first_video_intro(name):
    youtube_search = requests.get(
        'https://www.googleapis.com/youtube/v3/search?part=snippet' +
        '&type=video' +
        '&q=' + str(urllib.quote(name)) + "(Intro)"
        '&maxResults=1' +
        # '&topicId=/m/01xrzjs ' TODO Make this work
        '&key=' + YOUTUBE_API_KEY)

    reponse = youtube_search.content

    json_response = json.loads(reponse)
    video_id = json_response['items'][0]['id']['videoId']
    print video_id


class ParserOptions():
    parser = OptionParser(
        description="Grabs first search result from a query",
        prog='yt_query',
        usage='./%prog.py -q "query" '
        )
    parser.add_option("-q", "--query", action='store', dest='query', help='Wrapped in quotes')

    options, arguments = parser.parse_args()

    if not options.query:
        parser.error('Query not provided.')

    query = str(options.query.title())
    print query

    get_first_video(query)
