from django.shortcuts import render, redirect, get_object_or_404
from django.template import RequestContext, loader, Context, Template
from django.http import HttpResponse, Http404
from django.contrib.auth import authenticate, login, logout
from beacon_api.models import Scan, Floorplan, Beacon, Log
from django.contrib.auth.decorators import login_required

import logging
import json

logger = logging.getLogger(__name__)


def index(request):
    """
    Main page clients look at when loading
    beacon-adventure.herokuapp.com
    """
    return render(request, "index.html", {})

def print_logs(request):
    """
    beacon-adventure.herokuapp.com/logs/
    Loads the scan logs from the database
    and displays them in a user-friendly format
    """
    if request.user.is_authenticated():
        pass
    else:
        pass

    logs = Scan.objects.all()
    outstring = "<p>["
    for log in logs:
        outstring = "%s<br>    { 'log':%s, 'distance':%s, 'beacon':%s, time:%s }" % (outstring, log.log, log.calc_distance(), log.beacon, log.time)
    outstring = "%s <br>]</p>" % outstring
    return HttpResponse(outstring)

@login_required
def stores_index(request):
    stores = Floorplan.objects.filter(owner=request.user)

    return render(request, "store_index.html", {"store_list":stores})

@login_required
def store_detail(request, store_number):
    """
    Detail page for a given store.
    """
    print 'Store detail loaded'

    store = get_object_or_404(Floorplan, pk=store_number)
    beacon_list = Beacon.objects.filter(store=store)
    if store.owner != request.user:
        raise Http404
    return render(request, "store_detail.html", {"store":store, "beacon_list":beacon_list})

@login_required
def store_scans(request, store_number):
    """ Return a JSON-formatted representation of
        all of the scans at this store that we want to show.
        This will be formatted as an array of path arrays,
        each path array containing a set of objects with an
        x, y, and time property.
    """
    # calc_user_path isn't implemented so just return a piece of sample data
    # json_data = {}
    # json_data["result"] = "success"
    # json_data["paths"] = []
    # path1 = [
    # {
    #     "x":4,
    #     "y":4,
    #     "time":1398197183
    # },
    # {
    #     "x":5,
    #     "y":4,
    #     "time":1398197185
    # },
    # {
    #     "x":6,
    #     "y":4,
    #     "time":1398197187
    # },
    # {
    #     "x":6,
    #     "y":5,
    #     "time":1398197189
    # },
    # {
    #     "x":6,
    #     "y":6,
    #     "time":1398197191
    # },
    # {
    #     "x":6,
    #     "y":7,
    #     "time":1398197193
    # }]
    # path2 = [
    # {
    #     "x":12,
    #     "y":12,
    #     "time":1398197183
    # },
    # {
    #     "x":12,
    #     "y":10,
    #     "time":1398197185
    # },
    # {
    #     "x":12,
    #     "y":9,
    #     "time":1398197187
    # },
    # {
    #     "x":10,
    #     "y":9,
    #     "time":1398197189
    # },
    # {
    #     "x":9,
    #     "y":8,
    #     "time":1398197191
    # },
    # {
    #     "x":9,
    #     "y":9,
    #     "time":1398197193
    # }]
    # json_data["paths"].append(path1)
    # json_data["paths"].append(path2)
    # return HttpResponse(json.dumps(json_data))

    store = get_object_or_404(Floorplan, pk=store_number)
    logs = Log.objects.filter(store=store)
    json_data = {}
    json_data["result"] = "success"
    json_data["paths"] = []
    for log in logs:
        path = log.calc_user_path()
        json_path = []
        json_data["paths"].append(json_path)
        for location in path:
            x, y, time = location
            json_path.append({
                "x":x,
                "y":y,
                "time":time
            })
    return HttpResponse(json.dumps(json_data))
