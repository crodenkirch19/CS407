from django.shortcuts import render
from django.shortcuts import render
from django.template import RequestContext, loader, Context, Template
from django.http import HttpResponse
from beacon_api.models import Scan, Log, Beacon
from datetime import datetime

import json

import datetime

def index(request):
    api_version = 1.0
    methods_defined = ['send_scans']
    response = json.dumps({
        'api_version':api_version,
        'methods_defined':methods_defined
    })
    return HttpResponse(response)

def not_found(request):
    return error_response(404, 'no method found with this name')

def send_scans(request):
    # First extract data from the POST data
    if request.method != "POST" :
        return error_response(404, 'no method found with this name and method')
    params = request.POST
    try:
        token = params.token
        scans = params.scans
    except AttributeError:
        return error_response(400, 'missing attributes on input data')

    # Now we query the Google API and get a user id from this token
    # TODO

    # So we know we have a valid scan. Save it to the database.
    for scan_group in scans:
        for scan in scan_group:
            log = Log.objects.all()[0] # TODO make new log for each scan
            time = datetime.now() # TODO parse from time data
            distance = scan.dist

            # Make a beacon for this scan if one doesn't exist
            matching_beacons = Beacon.objects.filter(mac_address=scan.addr)
            if len(matching_beacons) == 0:
                b = Beacon(mac_address=scan.addr, store=Floorplan.objects.all()[0])
                location_x = 10
                location_y = 10
                b.save()
            else:
                b = matching_beacons[0]


            scan = Scan(log=log, beacon=b, distance=distance, time=time)
            scan.save()
    

    response = ''
    return HttpResponse(response)

def error_response(error_id, error_message):
    response = json.dumps({
        'error_id':error_id,
        'error_message':error_message
    })
    return HttpResponse(response)

