from django.shortcuts import render, redirect, get_object_or_404
from django.template import RequestContext, loader, Context, Template
from django.http import HttpResponse, Http404
from django.contrib.auth import authenticate, login, logout
from beacon_api.models import Scan, Floorplan, Beacon
from django.contrib.auth.decorators import login_required


def index(request):
    """
    Main page clients look at when loading
    beacon-adventure.herokuapp.com
    """

    # Respond to user login or logout event
    if request.POST:
        action = request.POST.get('action')
        if action == 'login':
            username = request.POST.get('username')
            password = request.POST.get('password')
            # user = authenticate(username=username, password=password)
            # if user is not None:
            #     if user.is_active:
            #         login(request, user)
            #         return redirect("/logs/")
        elif action == 'logout':
            logout(request)
            return redirect("/")
        else:
            return render(request, "index.html", {})
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

@login_required
def stores_index(request):
    stores = Floorplan.objects.filter(owner=request.user)

    return render(request, "store_index.html", { "store_list":stores })

@login_required
def store_detail(request, store_number):
    store = get_object_or_404(Floorplan, pk=store_number)
    beacon_list = Beacon.objects.filter(store=store)
    if store.owner != request.user:
        raise Http404
    return render(request, "store_detail.html", {"store":store, "beacon_list":beacon_list})