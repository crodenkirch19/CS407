from django.shortcuts import render
from django.template import RequestContext, loader, Context, Template
from django.http import HttpResponse

def index(request):
    template = loader.get_template('index.html')
    return HttpResponse(template.render(Context()))

def api_call(request, function_name):
    return HttpResponse("You called %s" % function_name)