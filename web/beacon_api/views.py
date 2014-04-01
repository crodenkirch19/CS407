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
    error_id = 404
    error_message = "no method found with this name"
    response = json.dumps({
        'error_id':error_id,
        'error_message':error_message
    })
    return HttpResponse(response)

def send_scans(response):
    pass