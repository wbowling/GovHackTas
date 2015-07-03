import requests
from local import YOUTUBE_API_KEY


def get_first_video(name):
    youtube_search = requests.get(
        'https://www.googleapis.com/youtube/v3/search?part' +
        '')
    return ''  # The initial results video ID (passed to get related vid)


def get_related_to_video(name):
    youtube_search = requests.get(
        'https://www.googleapis.com/youtube/v3/search?part' +
        '' +
        '&relatedToVideoId=' + get_first_video(name))

    return ''  # Return the related vid url or id?
