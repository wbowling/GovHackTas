import requests
from local import YOUTUBE_API_KEY


def get_first_video(name):
    youtube_search = requests.get(
        'https://www.googleapis.com/youtube/v3/search/' +
        '')