from django.shortcuts import render, redirect
from django.template import RequestContext, loader, Context, Template
from django.http import HttpResponse
from django.contrib.auth import authenticate, login, logout

def index(request):
    # Respond to user login event
    if request.POST:
        username = request.POST.get('username')
        password = request.POST.get('password')
        user = authenticate(username=username, password=password)
        if user is not None:
            if user.is_active:
                login(request, user)
                return redirect("/logs/")
    return render(request,"index.html",{})

    # template = loader.get_template('index.html')
    # return HttpResponse(template.render(Context()))

def logs(request):
    if request.user.is_authenticated():
        name = request.user.get_username()
        return HttpResponse("Hey great job %s you're authenticated! <br> Here are your logs man. You deserve them!" % name)
    else:
        return HttpResponse("Yo I'd authenticate first if I were you")