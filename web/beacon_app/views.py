from django.shortcuts import render, redirect
from django.template import RequestContext, loader, Context, Template
from django.http import HttpResponse
from django.contrib.auth import authenticate, login, logout
from beacon_api.models import Scan

def index(request):
    """
    Main page clients look at when loading
    beacon-adventure.herokuapp.com
    """
    # Respond to user login event
    if request.POST:
        username = request.POST.get('username')
        password = request.POST.get('password')
        user = authenticate(username=username, password=password)
        if user is not None:
            if user.is_active:
                login(request, user)
                return redirect("/logs/")
    return render(request, "index.html", {})

def logs(request):
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
        outstring = "%s<br>    { 'log':%s, 'distance':%s, 'beacon':%s, time:%s }" % (outstring, log.log, log.distance, log.beacon, log.time)
    outstring = "%s <br>]</p>" % outstring
    return HttpResponse(outstring)
