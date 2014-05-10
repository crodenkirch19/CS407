from django.shortcuts import render
from django.shortcuts import render
from django.template import RequestContext, loader, Context, Template
from django.http import HttpResponse
from beacon_api.models import Scan, Log, Beacon, Floorplan, MobileUser
from datetime import datetime
from django.views.decorators.csrf import csrf_exempt
import dateutil.parser
import simplejson

import logging
logger = logging.getLogger(__name__)

import random

import json

import datetime

@csrf_exempt
def index(request):
    """
    List all function in the API
    Called at becaon-adventure.herokuapp.com/api/
    """
    api_version = 1.0
    methods_defined = ['send_scans']
    response = json.dumps({
        'api_version':api_version,
        'methods_defined':methods_defined
    })
    return HttpResponse(response)

@csrf_exempt
def not_found(request):
    """

    """
    return error_response(404, 'no method found with this name')

@csrf_exempt
def send_scans(request):
    # First extract data from the POST data
    if request.method != "POST" :
        return error_response(404, 'no method found with this name and method')

    params = simplejson.loads(request.body)
    error = str(request.body)

    try:
        token = params["token"]
        scans = params["scans"]
    except AttributeError:
        return error_response(400, 'missing attributes on input data %s' % error )

    # Now we query the Google API and get a user id from this token
    # We'll use this token to name a userdata
    # TODO

    # TODO cross-reference a single beacon to find what store it corresponds to
    # We'll also use the userdata to make this log


    # So we know we have a valid scan. Save it to the database.
    for scan_group in scans:
        for scan in scan_group:
            log = Log(store=Floorplan.objects.all()[0], 
                      MobileUser=MobileUser.objects.all()[0])
            log.save()

            try:
                time = dateutil.parser.parse(scan["time"])
                distance = scan["dist"]
            except AttributeError:
                return error_response(400, 'missing attributes on input data')

            # If this beacon doesn't exist add it to the database
            matching_beacons = Beacon.objects.filter(mac_address=scan["addr"])
            if len(matching_beacons) == 0:
                b = Beacon(mac_address=scan["addr"],
                           store=Floorplan.objects.all()[0],
                           location_x=random.randint(1, 100),
                           location_y=random.randint(1, 100))
                b.save()
                print "==== New beacon added: %s ==== " % mac_address

                # return error_response(400, 'no beacon with the specified address was found')
            else:
                b = matching_beacons[0]


            scan = Scan(log=log, beacon=b, distance=distance, time=time)
            scan.save()
    


    response = r'{ "response":"success!" }'
    return HttpResponse(response)

@csrf_exempt
def error_response(error_id, error_message):
    response = json.dumps({
        'error_id':error_id,
        'error_message':error_message
    })
    return HttpResponse(response)

