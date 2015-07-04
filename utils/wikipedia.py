#!/usr/bin/python
import requests
import urllib
import re
import json
from bs4 import BeautifulSoup
from optparse import OptionParser

base_url = 'https://en.wikipedia.org/w/api.php?'


def subit(msg):
    subbed = re.sub("(\[.*?\])", "", msg)
    return subbed


def get_wikipedia_page_id(title):
    request = (
        base_url +
        'action=query' +
        '&titles=' + str(urllib.quote(title)) +
        '&prop=info'
        '&inprop=url'
        '&format=json')
    wikipedia_response = requests.get(request).content
    json_response = json.loads(wikipedia_response)
    page_json = json_response['query']['pages']

    page_id = page_json.keys()[0]
    page_url = page_json[page_id]['fullurl']

    return page_url


def get_wikipedia_info(title):
    page_url = get_wikipedia_page_id(title)
    request = (page_url)
    wikipedia_response = requests.get(request).content
    soup = BeautifulSoup(wikipedia_response)
    text_div = soup.find(id='mw-content-text')
    div_children = text_div.children
    for tag in div_children:
        if tag.name == 'p':
            print subit(tag.get_text())
            break


class ParserOptions():
    parser = OptionParser(
        description="Searches wikipedia for a series/movie title ",
        prog='wiki_query',
        usage='./%prog.py -t "title" '
        )
    parser.add_option("-t", "--title", action='store', dest='title', help='Wrapped in quotes')

    options, arguments = parser.parse_args()

    if not options.title:
        parser.error('Title not provided.')

    title = str(options.title)
    get_wikipedia_info(title)
