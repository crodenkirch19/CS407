from django.shortcuts import render
<<<<<<< HEAD
from django.http import Http404
from django.http import HttpResponse
from polls.models import Poll

def index(request):
    latest_poll_list = Poll.objects.all().order_by('-pub_date')[:5]
    context = {'latest_poll_list': latest_poll_list}
    return render(request, 'polls/index.html', context)
        
def detail(request, poll_id):
    poll = get_object_or_404(Poll, pk=poll_id)
    return render(request, 'polls/detail.html', {'poll': poll})
    
def results(request, poll_id):
    return HttpResponse("You're looking at the results of poll %s." % poll_id)

def vote(request, poll_id):
    return HttpResponse("You're voting on poll %s." % poll_id)
=======

from django.http import HttpResponse

def index(request):
    return HttpResponse("Hello, world. You're at the poll index.")
>>>>>>> 92575cfaca95af6a75fd43b2198411188798f593
