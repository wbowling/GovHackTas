import requests
import urllib
import json
from optparse import OptionParser

base_url = 'https://en.wikipedia.org/w/api.php?'


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
    print wikipedia_response
'''
class ParserOptions():
    parser = OptionParser(
        description="Searches wikipedia for a ",
        prog='yt_query',
        usage='./%prog.py -q "query" '
        )
    parser.add_option("-q", "--query", action='store', dest='query', help='Wrapped in quotes')

    options, arguments = parser.parse_args()

    if not options.query:
        parser.error('Query not provided.')

    query = str(options.query)
    title = ''
    #get_wikipedia_info(title)
'''
get_wikipedia_info('Cats')
