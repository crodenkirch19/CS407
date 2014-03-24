from django.conf.urls import patterns, include, url

from django.contrib import admin
admin.autodiscover()

urlpatterns = patterns('',
<<<<<<< HEAD
    url(r'^polls/', include('polls.urls', namespace="polls")),
=======
    url(r'^polls/', include('polls.urls')),
>>>>>>> 92575cfaca95af6a75fd43b2198411188798f593
    url(r'^admin/', include(admin.site.urls)),
)