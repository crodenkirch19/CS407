from django.shortcuts import render
from django.template import RequestContext, loader, Context, Template
from django.http import HttpResponse
import datetime

def index(request):
    now = datetime.datetime.now()
    html = "<html><body>It is now %s.</body></html>" % now
    template = loader.get_template('index.html')
    return HttpResponse(template.render(Context()))