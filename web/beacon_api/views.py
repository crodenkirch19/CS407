from django.shortcuts import render
from django.shortcuts import render
from django.template import RequestContext, loader, Context, Template
from django.http import HttpResponse

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
    

    response = ''
    return HttpResponse(response)

def error_response(error_id, error_message):
    response = json.dumps({
        'error_id':error_id,
        'error_message':error_message
    })
    return HttpResponse(response)

